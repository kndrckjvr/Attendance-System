package com.example.dev4.attendancesystem;

import java.util.ArrayList;

/**
 * Created by Jasmin Gutierrez on 29/06/2017.
 */

public class CourseClass {
    private String id;
    private String name;
    private String desc;
    private String section;
    private String prof_id;
    public static ArrayList<CourseClass> COURSE_LIST = new ArrayList<>();

    public CourseClass(String id, String name, String desc, String section, String prof_id) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.section = section;
        this.prof_id = prof_id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getSection() {
        return section;
    }

    public String getProf_id() {
        return prof_id;
    }
}
