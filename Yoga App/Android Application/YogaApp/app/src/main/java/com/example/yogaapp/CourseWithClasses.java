package com.example.yogaapp;

import java.util.ArrayList;

public class CourseWithClasses {

        private String userId;
        private ArrayList<Course> courses;

        public CourseWithClasses() {
            // Required empty public constructor
            courses = new ArrayList<>();
        }

        public CourseWithClasses(String userId, ArrayList<Course> courses) {
            this.userId = userId;
            this.courses = courses;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public ArrayList<Course> getCourses() {
            return courses;
        }

        public void setCourses(ArrayList<Course> courses) {
            this.courses = courses;
        }

        public void jsonFormat() {
            for (Course course : courses) {
                System.out.println(course.toString());
            }
        }

}
