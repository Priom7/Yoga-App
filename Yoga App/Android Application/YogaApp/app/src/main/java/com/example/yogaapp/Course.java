package com.example.yogaapp;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Course implements Serializable {

    private String dayOfWeek;
    private String time;
    private int capacity;
    private String duration;
    private String pricePerClass;
    private String typeOfCourse;
    private String description;

    private String courseName;

    private String courseID;

    private String courseInstructorName;

    private String courseInstructorRating;

    private String courseLevel;

    private String userId;

    private String Id;


    private ArrayList<ClassInfo> classInfos;


    // Add getters and setters
    public Course() {
        // Required empty public constructor
        classInfos = new ArrayList<>();
    }

    public Course(String dayOfWeek, String time, int capacity, String duration, String pricePerClass, String typeOfCourse, String description, String courseName, String courseID, String courseInstructorName, String courseInstructorRating, String courseLevel, String UserId, ArrayList<ClassInfo> classInfos, String Id) {
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.pricePerClass = pricePerClass;
        this.typeOfCourse = typeOfCourse;
        this.description = description;
        this.courseName = courseName;
        this.courseID = courseID;
        this.courseInstructorName = courseInstructorName;
        this.courseInstructorRating = courseInstructorRating;
        this.courseLevel = courseLevel;
        this.userId = UserId;
        this.classInfos = classInfos;
        this.Id = Id;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String UserId) {
        this.userId = UserId;
    }


    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPricePerClass() {
        return pricePerClass;
    }

    public void setPricePerClass(String pricePerClass) {
        this.pricePerClass = pricePerClass;
    }

    public String getTypeOfCourse() {
        return typeOfCourse;
    }

    public void setTypeOfCourse(String typeOfCourse) {
        this.typeOfCourse = typeOfCourse;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public ArrayList<ClassInfo> getClassInfos() {
        return classInfos;
    }

    public void setClassInfos(ArrayList<ClassInfo> classInfos) {
        this.classInfos = classInfos;
    }


    public void addClassInfo(ClassInfo classInfo) {
        // Check if classInfos is null before adding an element
        if (classInfos != null) {
            classInfos.add(classInfo);
        } else {
            // Log an error or handle the situation where classInfos is unexpectedly null
            Log.e("Course", "classInfos is null when adding ClassInfo");
        }
    }


}