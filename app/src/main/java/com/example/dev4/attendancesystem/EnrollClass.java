package com.example.dev4.attendancesystem;

import java.util.ArrayList;

/**
 * Created by Jasmin Gutierrez on 30/06/2017.
 */

public class EnrollClass {
    private String id;
    private String stud_id;
    private String course_id;
    public static ArrayList<EnrollClass> SHOW_ENROLL = new ArrayList<>();

    public EnrollClass(String id, String stud_id, String course_id) {
        this.id = id;
        this.stud_id = stud_id;
        this.course_id = course_id;
    }

    public String getId() {
        return id;
    }

    public String getStud_id() {
        return stud_id;
    }

    public String getCourse_id() {
        return course_id;
    }
}
