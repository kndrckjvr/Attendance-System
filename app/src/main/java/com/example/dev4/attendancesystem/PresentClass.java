package com.example.dev4.attendancesystem;

import java.util.ArrayList;

/**
 * Created by Jasmin Gutierrez on 29/06/2017.
 */

public class PresentClass {
    private String id;
    private String atten_id;
    private String stud_id;
    private String course_id;
    private String status;
    public static ArrayList<PresentClass> SHOW_PRESENT = new ArrayList<>();

    public PresentClass(String id, String atten_id, String stud_id, String status, String course_id) {
        this.id = id;
        this.atten_id = atten_id;
        this.stud_id = stud_id;
        this.course_id = course_id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getAtten_id() {
        return atten_id;
    }

    public String getStud_id() {
        return stud_id;
    }

    public String getStatus() {
        return status;
    }

    public String getCourse_id() {
        return course_id;
    }
}
