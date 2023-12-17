package com.example.yogaapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class InventoryFragment extends Fragment implements DataTransferListener {

    private FirebaseFirestore db;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    private TextInputEditText courseTitleEditText, typeOfCourseEditText, pricePerEditText,
            durationEditText, capacityEditText, descriptionEditText, classTitleEditText, classTimeEditText;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LinearLayout container;
    private String mParam1;
    private String mParam2;
    private View rootView;
    private String selectedDay = "SUNDAY";
    private static final int REQUEST_IMAGE_PICKER = 3;

    private ImageView imageView;
    private Button imageUploadButton;
    private ArrayList<ClassInfo> classInfoList = new ArrayList<>();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private Course course;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    handleImagePicked(data);
                }
            });


    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InventoryFragment() {
        // Required empty public constructor
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            course = (Course) getArguments().getSerializable("course");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            String receivedData = bundle.getString("key");
            // Use the receivedData as needed
        }
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_create_course, container, false);
        rootView = fragmentView;

        // Initialize views
        Button timePickerButton = fragmentView.findViewById(R.id.timePickerButton);
        Spinner daySpinner = fragmentView.findViewById(R.id.daySpinner);

        // Initialize views
        courseTitleEditText = fragmentView.findViewById(R.id.courseTitleEditText);
        typeOfCourseEditText = fragmentView.findViewById(R.id.typeOfCourseEditText);
        pricePerEditText = fragmentView.findViewById(R.id.pricePerEditText);
        durationEditText = fragmentView.findViewById(R.id.durationEditText);
        capacityEditText = fragmentView.findViewById(R.id.capacityEditText);
        descriptionEditText = fragmentView.findViewById(R.id.descriptionEditText);
        classTimeEditText = fragmentView.findViewById(R.id.classTimeEditText);
        Button saveButton = fragmentView.findViewById(R.id.saveButton);
        if (course != null && !TextUtils.isEmpty(course.getId())) {
            courseTitleEditText.setText(course.getCourseName());
            typeOfCourseEditText.setText(course.getTypeOfCourse());
            pricePerEditText.setText(course.getPricePerClass());
            durationEditText.setText(course.getDuration());
            capacityEditText.setText(String.valueOf(course.getCapacity()));
            descriptionEditText.setText(course.getDescription());
            classTimeEditText.setText(course.getTime());
            saveButton.setText("Update Course");
            saveButton.setTextColor(getResources().getColor(R.color.btn_edit_color));
            renderClassesData(course);
        } else {
            // Handle the case when course is null or its ID is empty
            Log.e("InventoryTest", "Course is null or its ID is empty");
        }



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.days_of_week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);

        // Set a listener for item selection
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item
                 selectedDay = parentView.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), "Selected Day: " + selectedDay, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });


        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select Class time")
                .build();

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = picker.getHour();
                int minute = picker.getMinute();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                Date selectedTime = calendar.getTime();

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String formattedTime = sdf.format(selectedTime);
                classTimeEditText.setText(formattedTime);
                Log.d("SelectedTime", formattedTime);
                Toast.makeText(getContext(), formattedTime, Toast.LENGTH_SHORT).show();
                String selectedDay = daySpinner.getSelectedItem().toString();
                Toast.makeText(getContext(), "Selected Day: " + selectedDay, Toast.LENGTH_SHORT).show();


            }

        });

        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getParentFragmentManager(), "TimeTest");
            }
        });

         this.container = fragmentView.findViewById(R.id.container);
        Button addClassButton = fragmentView.findViewById(R.id.addClassButton);
        // Set click listener for Add Class button
        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDay = daySpinner.getSelectedItem().toString();
                ClassInfo classInfo = new ClassInfo();
                addClassInputField(selectedDay, classInfo);
            }
        });



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( validateInputs(courseTitleEditText.getText().toString().trim(), typeOfCourseEditText.getText().toString().trim(), pricePerEditText.getText().toString().trim(), durationEditText.getText().toString().trim(), capacityEditText.getText().toString().trim(), descriptionEditText.getText().toString().trim(), classTimeEditText.getText().toString().trim(), selectedDay)){
                    mAuth = FirebaseAuth.getInstance();
                    currentUser = mAuth.getCurrentUser();
                    retrieveDataFromDynamicFields();
                    String selectedDay = daySpinner.getSelectedItem().toString();
                    String courseTitle = courseTitleEditText.getText().toString().trim();
                    String typeOfCourse = typeOfCourseEditText.getText().toString().trim();
                    String pricePer = pricePerEditText.getText().toString().trim();
                    String duration = durationEditText.getText().toString().trim();
                    String capacity = capacityEditText.getText().toString().trim();
                    String descriptionEt = descriptionEditText.getText().toString().trim();
                    String classTime = classTimeEditText.getText().toString().trim();



                    if(course != null && !TextUtils.isEmpty(course.getId())) {

                        // Create a Course object
                        String courseId = course.getId().toString();// Get the course ID from your data
                        Course course = new Course();
                        course.setCourseName(courseTitle);
                        course.setDayOfWeek(selectedDay);
                        course.setTypeOfCourse(typeOfCourse);
                        course.setPricePerClass(pricePer);
                        course.setDuration(duration);
                        course.setCapacity(Integer.parseInt(capacity));
                        course.setDescription(descriptionEt);
                        course.setTime(classTime);
                        course.setId(courseId);
                        course.setUserId(currentUser.getUid());
                        showConfirmationDialog(course, "Update");
                    }
                    else {
                        Course course = new Course();
                        course.setCourseName(courseTitle);
                        course.setDayOfWeek(selectedDay);
                        course.setTypeOfCourse(typeOfCourse);
                        course.setPricePerClass(pricePer);
                        course.setDuration(duration);
                        course.setCapacity(Integer.parseInt(capacity));
                        course.setDescription(descriptionEt);
                        course.setTime(classTime);
                        course.setUserId(currentUser.getUid());
                        showConfirmationDialog(course, "Save");
                    }



                }
                else {
                    Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                }


            }
        });


        return fragmentView;
    }
   public boolean validateInputs (String courseTitle, String typeOfCourse, String pricePer, String duration, String capacity, String description, String classTime, String selectedDay) {
        if (courseTitle.isEmpty()) {
            courseTitleEditText.setError("Course Title is required");
            courseTitleEditText.requestFocus();
            return false;
        }
        if (typeOfCourse.isEmpty()) {
            typeOfCourseEditText.setError("Type of Course is required");
            typeOfCourseEditText.requestFocus();
            return false;
        }
        if (pricePer.isEmpty()) {
            pricePerEditText.setError("Price per class is required");
            pricePerEditText.requestFocus();
            return false;
        }
        if (duration.isEmpty()) {
            durationEditText.setError("Duration is required");
            durationEditText.requestFocus();
            return false;
        }
        if (capacity.isEmpty()) {
            capacityEditText.setError("Capacity is required");
            capacityEditText.requestFocus();
            return false;
        }

        if (classTime.isEmpty()) {
            classTimeEditText.setError("Class time is required");
            classTimeEditText.requestFocus();
            return false;
        }
        if (selectedDay.isEmpty()) {
            Toast.makeText(getContext(), "Please select a day", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            Toast.makeText(getContext(), "All inputs are valid", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void openImagePicker(ImageView imageView) {
        Log.d("ImagePicker", "handleImagePicked: ");
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(galleryIntent);
    }
    private void handleImagePicked(Intent data) {
        Log.d("ImagePicker", "handleImagePicked: " + data);
        if (data != null) {

             Uri imageUri = data.getData();
             imageView = rootView.findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);



        }

    }
public void renderClassesData(Course course) {
        ArrayList<ClassInfo> classInfos = course.getClassInfos();

        //set timeout for the data to load
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        //do something
                        for(ClassInfo classInfo : classInfos) {
                            addClassInputField("SUNDAY", classInfo);
                        }
                    }
                },
                3000);

}


private void addClassInputField(String selectedDay, ClassInfo classInfo) {
        // Inflate the layout for a single class input
        View classInput = LayoutInflater.from(requireContext()).inflate(R.layout.class_inputs, container, false);
        MaterialButton datePickerButton = classInput.findViewById(R.id.datePickerButton);
        TextInputEditText classDateEditText = classInput.findViewById(R.id.classDateEditText);
        Button imageUploadButton = classInput.findViewById(R.id.imageUploadButton);
        ImageView imageView = classInput.findViewById(R.id.imageView);
        EditText classDescriptionEditText = classInput.findViewById(R.id.classDescriptionEditText);
        EditText classInstructorEditText  = classInput.findViewById(R.id.classInstructorEditText);
        EditText classInstructorRatingEditText = classInput.findViewById(R.id.classInstructorRatingEditText);
        EditText classInstructorEmailEditText = classInput.findViewById(R.id.classInstructorEmailEditText);
        EditText  classTitleEditText = classInput.findViewById(R.id.classTitleEditText);



        imageUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open image picker
                Toast.makeText(getContext(), "Image Upload", Toast.LENGTH_SHORT).show();
                openImagePicker(imageView);
            }
        });


        int targetDayOfWeek = 1;
        switch (selectedDay) {
            case "SUNDAY":
                targetDayOfWeek = Calendar.SUNDAY;
                break;
            case "MONDAY":
                targetDayOfWeek = Calendar.MONDAY;
                break;
            case "TUESDAY":
                targetDayOfWeek = Calendar.TUESDAY;
                break;
            case "WEDNESDAY":
                targetDayOfWeek = Calendar.WEDNESDAY;
                break;
            case "THURSDAY":
                targetDayOfWeek = Calendar.THURSDAY;
                break;
            case "FRIDAY":
                targetDayOfWeek = Calendar.FRIDAY;
                break;
            case "SATURDAY":
                targetDayOfWeek = Calendar.SATURDAY;
                break;
        }
        WeekdayDateValidator.setupDatePickerClickListener(datePickerButton, classDateEditText, targetDayOfWeek, getParentFragmentManager());

        // Find the "Remove" button in the inflated layout
        ImageView removeButton = classInput.findViewById(R.id.removeButton);

        if (classInfo != null && !TextUtils.isEmpty(classInfo.getClassDate())) {
            classDateEditText.setText(classInfo.getClassDate());
            classDescriptionEditText.setText(classInfo.getClassDescription());
            classInstructorEditText.setText(classInfo.getClassInstructor());
            classInstructorRatingEditText.setText(classInfo.getClassInstructorRating());
            classInstructorEmailEditText.setText(classInfo.getClassInstructorEmail());
            classTitleEditText.setText(classInfo.getClassName());

        } else {
            // Handle the case when course is null or its ID is empty
            Log.e("InventoryTest", "Class is null or its ID is empty");
        }

        // Set OnClickListener for the "Remove" button
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the parent layout (CardView) from the container
                container.removeView((View) v.getParent().getParent().getParent());

            }

        });
        // Add the class input to the container
        container.addView(classInput);
    }
    private void retrieveDataFromDynamicFields() {
        classInfoList.clear(); // Clear the list to avoid duplicates

        // Iterate through the container to find dynamically added class input fields
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);

            if (child instanceof CardView) {
                // Assuming the CardView is your dynamically added class input field layout
                CardView classInputLayout = (CardView) child;

                // Find the EditText views for class name and class time
                EditText classNameEditText = classInputLayout.findViewById(R.id.classTitleEditText);
                EditText classDescriptionEditText = classInputLayout.findViewById(R.id.classDescriptionEditText);
                EditText classDateEditText = classInputLayout.findViewById(R.id.classDateEditText);
                EditText classInstructorEditText  = classInputLayout.findViewById(R.id.classInstructorEditText);
                EditText classInstructorRatingEditText = classInputLayout.findViewById(R.id.classInstructorRatingEditText);
                EditText classInstructorEmailEditText = classInputLayout.findViewById(R.id.classInstructorEmailEditText);


                // Retrieve data from EditText views
                String className = classNameEditText.getText().toString().trim();
                String classDate = classDateEditText.getText().toString().trim();
                String classDescription = classDescriptionEditText.getText().toString().trim();
                String classInstructor = classInstructorEditText.getText().toString().trim();
                String classInstructorRating = classInstructorRatingEditText.getText().toString().trim();
                String classInstructorEmail = classInstructorEmailEditText.getText().toString().trim();


                // Create a ClassInfo object and add it to the list
                ClassInfo classInfo = new ClassInfo();
                classInfo.setClassName(className);
                classInfo.setClassDate(classDate);
                classInfo.setClassDescription(classDescription);
                classInfo.setClassInstructor(classInstructor);
                classInfo.setClassInstructorRating(classInstructorRating);
                classInfo.setClassInstructorEmail(classInstructorEmail);

                classInfoList.add(classInfo);
            }
        }

    }
    private void showConfirmationDialog(Course course, String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(action + " Confirmation");
        builder.setMessage("Are you sure you want to save these classes?" + "\n" + "Course Details :" + "\n" + "Course Title: " + course.getCourseName() + "\n" + "Type of Course: " + course.getTypeOfCourse() + "\n" + "Price per class: " + course.getPricePerClass() + "\n" + "Duration: " + course.getDuration() + "\n" + "Capacity: " + course.getCapacity() + "\n" + "Description: " + course.getDescription() + "\n" + "Class Time: " + course.getTime() + "\n" + "Day of Week: " + course.getDayOfWeek() + "\n");

        // Set positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the positive button click (e.g., save data)
                // For example, you can call a method to save the data to Firebase Firestore
                if(action.equals("Save")) {
                    saveCourseToFirebase(course);
                }
                else if (action.equals("Update")) {
                    Toast.makeText(getContext(), "Course updated successfully" + course.getId(), Toast.LENGTH_SHORT).show();
                    updateCourseToFirebase(course);
                }
                container.removeAllViews();
                classInfoList.clear();


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
    private void updateCourseToFirebase(Course course) {
        // Get a new write batch
        // Create a WriteBatch
        WriteBatch batch = db.batch();

        // Set the value of newCourseRef

        Map<String, Object> courseData = new HashMap<>();
        courseData.put("courseName", course.getCourseName());
        courseData.put("dayOfWeek", course.getDayOfWeek());
        courseData.put("typeOfCourse", course.getTypeOfCourse());
        courseData.put("pricePerClass", course.getPricePerClass());
        courseData.put("duration", course.getDuration());
        courseData.put("capacity", course.getCapacity());
        courseData.put("description", course.getDescription());
        courseData.put("time", course.getTime());
        courseData.put("userId", course.getUserId());
        DocumentReference classRef = db.collection("courses").document(course.getId());
        classRef.set(courseData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Class updated successfully
                        Toast.makeText(getContext(), "Course details updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });

        for (ClassInfo classInfo : classInfoList) {
            // Create a Map to represent the class data
            Map<String, Object> classData = new HashMap<>();
            classData.put("className", classInfo.getClassName());
            classData.put("courseId", course.getId()); // Use the reference ID
            classData.put("classDate", classInfo.getClassDate());
            classData.put("classDescription", classInfo.getClassDescription());
            classData.put("classInstructor", classInfo.getClassInstructor());
            classData.put("classInstructorRating", classInfo.getClassInstructorRating());
            classData.put("classInstructorEmail", classInfo.getClassInstructorEmail());


            // Create a reference without adding to Firestore
            DocumentReference newClassRef = db.collection("classes").document();
            batch.set(newClassRef, classData);
        }

        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Course details updated!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveCourseToFirebase(Course course) {

        // Get a new write batch
        // Create a WriteBatch
        WriteBatch batch = db.batch();

// Set the value of newCourseRef
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("courseName", course.getCourseName());
        courseData.put("dayOfWeek", course.getDayOfWeek());
        courseData.put("typeOfCourse", course.getTypeOfCourse());
        courseData.put("pricePerClass", course.getPricePerClass());
        courseData.put("duration", course.getDuration());
        courseData.put("capacity", course.getCapacity());
        courseData.put("description", course.getDescription());
        courseData.put("time", course.getTime());
        courseData.put("userId", course.getUserId());

        DocumentReference newCourseRef = db.collection("courses").document(); // Create a reference without adding to Firestore
        batch.set(newCourseRef, courseData);

        for (ClassInfo classInfo : classInfoList) {
            // Create a Map to represent the class data
            Map<String, Object> classData = new HashMap<>();
            classData.put("className", classInfo.getClassName());
            classData.put("courseId", newCourseRef.getId()); // Use the reference ID
            classData.put("classDate", classInfo.getClassDate());
            classData.put("classDescription", classInfo.getClassDescription());
            classData.put("classInstructor", classInfo.getClassInstructor());
            classData.put("classInstructorRating", classInfo.getClassInstructorRating());
            classData.put("classInstructorEmail", classInfo.getClassInstructorEmail());


            // Create a reference without adding to Firestore
            DocumentReference newClassRef = db.collection("classes").document();
            batch.set(newClassRef, classData);
        }

// Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Course details saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error saving course details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    @Override
    public void onDataTransfer(Course data) {

        Log.i("CourseAdapterDashboard", "onDataTransfer: " + data.getDayOfWeek());
    }


}
