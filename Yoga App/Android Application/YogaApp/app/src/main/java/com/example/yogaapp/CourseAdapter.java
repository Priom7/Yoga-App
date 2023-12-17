package com.example.yogaapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder>  {
    private Context context;

    private ArrayList<Course> courseList;
    private DataTransferListener courseClickListener;

    // Other adapter code...

    public CourseAdapter( ) {


    }
    public void setCourseClickListener(DataTransferListener listener) {
        this.courseClickListener = listener;
    }


    public CourseAdapter(Context context, ArrayList<Course> courseList ) {
        this.context = context;
        this.courseList = courseList;

        Log.i("CourseAdapter", "CourseAdapter: " + courseList.size() + " courses");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);

        return new ViewHolder(view);
    }


@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Course course = courseList.get(position);
    if (course.getCourseName() != null) {
        holder.courseName.setText(course.getCourseName());
        holder.capacity.setText(String.valueOf(course.getCapacity()));
        holder.priceTV.setText(String.valueOf(course.getPricePerClass()));
        holder.dayOfWeekTV.setText(course.getDayOfWeek());
        ArrayList<ClassInfo> classInfoList = course.getClassInfos();
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(course, holder, v, position);

            }
        });
        for (ClassInfo classInfo : classInfoList) {

            addClassInputField(classInfo, holder, course.getPricePerClass(), course.getTime(), position);

        }

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseId = course.getId().toString();// Get the course ID from your data

                // Notify the fragment through the interface
                if (courseClickListener != null) {
                    courseClickListener.onDataTransfer(course);
                    Log.i("CourseAdapter", "Course ID: " + courseId);

                }
            }
        });
    } else {
        Log.e("CourseAdapter", "Course name is null for position: " + position);
    }

}

    private void showConfirmationDialog(Course course, ViewHolder holder, View view, int position ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure you want to Delete this Course? (All classes associate with is course will also be delete!)" + "\n" + "Course Details :" + "\n" + "Course Title: " + course.getCourseName() + "\n" + "Type of Course: " + course.getTypeOfCourse() + "\n" + "Price per class: " + course.getPricePerClass() + "\n" + "Duration: " + course.getDuration() + "\n" + "Capacity: " + course.getCapacity() + "\n" + "Description: " + course.getDescription() + "\n" + "Class Time: " + course.getTime() + "\n" + "Day of Week: " + course.getDayOfWeek() + "\n");

        // Set positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the positive button click (e.g., save data)
                // For example, you can call a method to save the data to Firebase Firestore
                deleteClassesForCourse(course.getId());

                // Step 2: Delete the course document
                deleteCourse(course.getId(), holder, view, position);


            }
        });




        // Set negative button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the negative button click (e.g., do nothing)
            }
        });

        // Show the dialog
        builder.show();
    }

    private void deleteClassesForCourse(String courseId) {
        // Create a reference to the "classes" collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference classesRef = db.collection("classes");

        // Query classes with the specified courseId
        Query query = classesRef.whereEqualTo("courseId", courseId);

        // Delete each class document
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            WriteBatch batch = db.batch();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                DocumentReference classRef = classesRef.document(document.getId());
                batch.delete(classRef);
            }
            // Commit the batch to delete all classes
            batch.commit();
        }).addOnFailureListener(e -> {
            // Handle errors
            Log.e("Firestore", "Error deleting classes for course: " + e.getMessage());
        });
    }
    public void removeItem(int position) {
        // Remove the item from your list
        courseList.remove(position);

        // Notify the adapter that an item has been removed
        notifyItemRemoved(position);
    }

    private void deleteCourse(String courseId, ViewHolder holder, View view, int position) {
        // Create a reference to the "courses" collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference courseRef = db.collection("courses").document(courseId);

        // Delete the course document
        courseRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Handle success, if needed
                    removeItem(position);
                    Toast.makeText(context, "Course deleted successfully", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("Firestore", "Error deleting course: " + e.getMessage());
                });
    }


    private void addClassInputField (ClassInfo classInfo, ViewHolder holder, String pricePerClass, String time, int position) {
        // Inflate the layout for a single class input
        LinearLayout container = holder.itemView.findViewById(R.id.classLinearLayout);

        View classInput = LayoutInflater.from(context).inflate(R.layout.class_card, null);

        TextView instructorName = classInput.findViewById(R.id.instructorNameIV);
        TextView instructor_rating = classInput.findViewById(R.id.instructorRatingTV);
        TextView courseDateTV = classInput.findViewById(R.id.courseDateTV);
        TextView pricePerClassTV = classInput.findViewById(R.id.pricePerClassTV);
        TextView classTimeTV = classInput.findViewById(R.id.classTimeTV);
        ImageView deleteClassBtn = classInput.findViewById(R.id.removeClassButton);
        Button profileBtn = classInput.findViewById(R.id.btn_viewProfile);

        instructorName.setText(classInfo.getClassInstructor());
        instructor_rating.setText(classInfo.getClassInstructorRating());
        courseDateTV.setText(classInfo.getClassDate());
        pricePerClassTV.setText(pricePerClass);
        classTimeTV.setText(time);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String classInstructorEmail = classInfo.getClassInstructorEmail().toString();// Get the course ID from your data

                // fetch all classes by classInstructorEmail
                // Notify the fragment through the interface
                classesWithclassInstructorEmail(classInstructorEmail);
            }
        });


        deleteClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialogForClass(classInfo, classInfo.getId(),container, classInput);

            }
        });

        // Add the class input to the container

        container.addView(classInput);
    }

    public void classesWithclassInstructorEmail( String classInstructorEmail) {
        ArrayList<ClassInfo> instructorClassInfoList = new ArrayList<>();
        // Assuming db is your FirebaseFirestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query classes with the specified classInstructorEmail
        Query query = db.collection("classes").whereEqualTo("classInstructorEmail", classInstructorEmail);

        // Delete each class document
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ClassInfo classInfo = document.toObject(ClassInfo.class);
                instructorClassInfoList.add(classInfo);
            }
            showClassListByInstructor(instructorClassInfoList);
            Toast.makeText(context, "Class Instructor Email: " + instructorClassInfoList, Toast.LENGTH_SHORT).show();


        }).addOnFailureListener(e -> {
            // Handle errors
            Log.e("Firestore", "Error deleting classes for course: " + e.getMessage());
        });
    }

    public void showClassListByInstructor(ArrayList<ClassInfo> instructorClassInfoList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String jsonString = gson.toJson(instructorClassInfoList);

            builder.setTitle("Instructor List Of Classes");
            builder.setMessage("Class Instructor: " + jsonString);


        // Set positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the positive button click (e.g., save data)
                // For example, you can call a method to save the data to Firebase Firestore

                // Step 2: Delete the course document



            }
        });

        // Set negative button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the negative button click (e.g., do nothing)
            }
        });

        // Show the dialog
        builder.show();
    }

    public void showConfirmationDialogForClass(ClassInfo classInfo, String classId, LinearLayout container, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure you want to Delete this Class? " + "\n" + "Class Details :" + "\n" + "Class Instructor: " + classInfo.getClassInstructor() + "\n" + "Class Instructor Rating: " + classInfo.getClassInstructorRating() + "\n" + "Class Date: " + classInfo.getClassDate() + "\n");

        // Set positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the positive button click (e.g., save data)
                // For example, you can call a method to save the data to Firebase Firestore

                // Step 2: Delete the course document
                deleteClass(classId, container, view);

            }
        });

        // Set negative button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the negative button click (e.g., do nothing)
            }
        });

        // Show the dialog
        builder.show();
    }



    private void deleteClass(String classId, LinearLayout container, View view) {
        // Create a reference to the "courses" collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference classRef = db.collection("classes").document(classId);

        // Delete the course document
        classRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Handle success, if needed
                    container.removeView(view);
                    Toast.makeText(context, "Class deleted successfully", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("Firestore", "Error deleting course: " + e.getMessage());
                });
    }




    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        TextView courseName;
        TextView capacity;
        TextView class_day, priceTV, dayOfWeekTV;
        Button editButton;
        Button deleteButton;


        // Add references to other views here

        public ViewHolder(View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.courseNameTV);
            editButton = itemView.findViewById(R.id.btn_edit_course);
            deleteButton = itemView.findViewById(R.id.btn_course_delete);
            capacity = itemView.findViewById(R.id.capcityTV);
            priceTV = itemView.findViewById(R.id.priceTV);
            class_day = itemView.findViewById(R.id.courseDateTV);
            dayOfWeekTV = itemView.findViewById(R.id.dayOfWeekTV);
            deleteButton = itemView.findViewById(R.id.btn_course_delete);

        }
    }
}

