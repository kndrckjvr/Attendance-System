package com.example.dev4.attendancesystem;

import java.util.ArrayList;

/**
 * Created by Jasmin Gutierrez on 29/06/2017.
 */

public class AttendanceClass {
    private String id;
    private String course_id;
    private String date;
    public static ArrayList<AttendanceClass> SHOW_ATTENDANCE = new ArrayList<>();

    public AttendanceClass(String id, String course_id, String date) {
        this.id = id;
        this.course_id = course_id;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public String getDate() {
        return date;
    }
}
