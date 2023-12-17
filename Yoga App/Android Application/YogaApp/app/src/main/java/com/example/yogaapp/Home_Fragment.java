package com.example.yogaapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home_Fragment extends Fragment {

    FirebaseAuth mAuth;
    Button btn_logout;
    TextView tv_welcome;
    FirebaseUser currentUser;
    View rootView;
    private ArrayList <Course> courseList;
    private ArrayList <ClassInfo> courseClassList;
    private CourseWithClasses dataForCloud;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Home_Fragment newInstance(String param1, String param2) {
        Home_Fragment fragment = new Home_Fragment();
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
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_home_, container, false);
        rootView = inflater.inflate(R.layout.fragment_home_, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        tv_welcome = rootView.findViewById(R.id.tv_welcome);
        btn_logout = rootView.findViewById(R.id.btn_logout);
        if (currentUser == null) {
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            tv_welcome.setText("Welcome " + currentUser.getEmail());
        }
        btn_logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        });



        courseList = new ArrayList<>();
        courseClassList = new ArrayList<>();
        dataForCloud = new CourseWithClasses();

        FirebaseFirestore db = FirebaseFirestore.getInstance();


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
                                                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                                    dataForCloud.setUserId(currentUser.getUid());
                                                    dataForCloud.setCourses(courseList);
                                                    String jsonString = gson.toJson(dataForCloud);

                                                    TextView jsonTV = rootView.findViewById(R.id.jsonDataTV);
                                                    jsonTV.setText(jsonString);
                                                    Button uploadToCloudBtn = rootView.findViewById(R.id.uploadToCloudBtn);

                                                    uploadToCloudBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            ApiClient.postData("https://stuiis.cms.gre.ac.uk/COMP1424CoreWS/comp1424cw/SubmitClasses", jsonString, new ApiClient.ApiCallback() {
                                                                @Override
                                                                public void onSuccess(String result) {
                                                                    // Handle the successful API response
                                                                    Toast.makeText(getContext(), "Data Upload Successful " + result, Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void onFailure(Exception e) {
                                                                    // Handle API call failure
                                                                    // 'e' contains the exception details
                                                                    Toast.makeText(getContext(), "Data Upload failed with error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });


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
}