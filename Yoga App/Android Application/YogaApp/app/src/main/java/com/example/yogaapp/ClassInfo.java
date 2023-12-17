package com.example.yogaapp;

public class ClassInfo {
    private String className;
    private String classDescription;
    private String classTime;
    private String classDate;
    private String classInstructor;
    private String classLocation;
    private String classType;
    private String courseId;

    private String classInstructorImage;

    private String classInstructorBio;

    private String classInstructorRating;
    private String classInstructorEmail;

    private String Id;


    public ClassInfo() {
    }

    public ClassInfo(String className, String classDescription, String classTime, String classDate, String classInstructor, String classLocation, String classType, String courseId, String classInstructorImage, String classInstructorBio, String classInstructorRating, String classInstructorEmail, String Id) {
        this.className = className;
        this.classDescription = classDescription;
        this.classTime = classTime;
        this.classDate = classDate;
        this.classInstructor = classInstructor;
        this.classLocation = classLocation;
        this.classType = classType;
        this.courseId = courseId;
        this.classInstructorImage = classInstructorImage;
        this.classInstructorBio = classInstructorBio;
        this.classInstructorRating = classInstructorRating;
        this.classInstructorEmail = classInstructorEmail;
        this.Id = Id;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassDescription() {
        return classDescription;
    }

    public void setClassDescription(String classDescription) {
        this.classDescription = classDescription;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassDate() {
        return classDate;
    }

    public void setClassDate(String classDate) {
        this.classDate = classDate;
    }

    public String getClassInstructor() {
        return classInstructor;
    }

    public void setClassInstructor(String classInstructor) {
        this.classInstructor = classInstructor;
    }

    public String getClassLocation() {
        return classLocation;
    }

    public void setClassLocation(String classLocation) {
        this.classLocation = classLocation;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getClassInstructorImage() {
        return classInstructorImage;
    }

    public void setClassInstructorImage(String classInstructorImage) {
        this.classInstructorImage = classInstructorImage;
    }

    public String getClassInstructorBio() {
        return classInstructorBio;
    }

    public void setClassInstructorBio(String classInstructorBio) {
        this.classInstructorBio = classInstructorBio;
    }

    public String getClassInstructorRating() {
        return classInstructorRating;
    }

    public void setClassInstructorRating(String classInstructorRating) {
        this.classInstructorRating = classInstructorRating;
    }

    public String getClassInstructorEmail() {
        return classInstructorEmail;
    }

    public void setClassInstructorEmail(String classInstructorEmail) {
        this.classInstructorEmail = classInstructorEmail;
    }
}
