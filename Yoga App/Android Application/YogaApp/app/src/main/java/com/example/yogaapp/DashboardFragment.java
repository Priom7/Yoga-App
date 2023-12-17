package com.example.yogaapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements DataTransferListener {
    View rootView;
    CourseAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RecyclerView recyclerView;
    private ArrayList<Course> courseList;
    private ArrayList<ClassInfo> courseClassList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        courseList = new ArrayList<>();
        courseClassList = new ArrayList<>();

//        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView); // Replace with your RecyclerView's ID
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // Use LinearLayoutManager or GridLayoutManager as needed

        recyclerView = rootView.findViewById(R.id.recyclerView);
        courseList = new ArrayList<>();



        // Assuming db is your FirebaseFirestore instance

        db.collection("courses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot courseDocument : task.getResult()) {
                                Course course = courseDocument.toObject(Course.class);
                                String courseId = courseDocument.getId(); // Get the document ID as courseId
                                course.setId(courseId);
                                courseList.add(course);
                                // Now, query classes with the courseId
                                db.collection("classes")
                                        .whereEqualTo("courseId", courseId)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot classDocument : task.getResult()) {
                                                        // Here, you can handle each class associated with the current course
                                                        ClassInfo currentClass = classDocument.toObject(ClassInfo.class);
                                                        // Do something with the class
                                                        String classId = classDocument.getId(); // Get the document ID as classId
                                                        currentClass.setId(classId);
                                                        courseClassList.add(currentClass);
                                                        course.addClassInfo(currentClass);
                                                    }
                                                    Log.i( "CourseWithClases: ", "data" + course.getCourseName());
                                                    adapter = new CourseAdapter(getContext(), courseList);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                    adapter.setCourseClickListener(DashboardFragment.this);
                                                    recyclerView.setAdapter(adapter);

                                                } else {
                                                    Log.e("FirestoreQuery", "Error getting classes: " + task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.e("FirestoreQuery", "Error getting courses: " + task.getException());
                        }
                    }
                });




        return rootView;
    }

    @Override
    public void onDataTransfer(Course data) {

        // Handle the data transfer (course ID) here
        // Change the layout or navigate to the InventoryFragment
        // using FragmentTransaction
        // For example, replace the current fragment with InventoryFragment
        InventoryFragment inventoryFragment = new InventoryFragment();
        loadFragment(inventoryFragment, data);
        Log.i("DashboardFragment", "Course ID: " + data);

    }
    void loadFragment(Fragment fragment, Course course) {
        Bundle data = new Bundle();
        data.putSerializable("course", course);

        fragment.setArguments(data);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.relativelayout, fragment)
                .commit();
    }

}