package com.example.dev4.attendancesystem;

import java.util.ArrayList;

/**
 * Created by KendrickCosca on 6/30/2017.
 */

public class StudentClass {
    private String id;
    private String name;
    private String number;
    private String serial;
    private String image;
    private int absent;
    public static ArrayList<StudentClass> STUDENT_LIST = new ArrayList<>();

    public StudentClass(String id, String name, String number, String serial, String image) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.serial = serial;
        this.image = image;
    }

    public StudentClass(String id, String name, String number, String serial, String image, int absent) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.serial = serial;
        this.image = image;
        this.absent = absent;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getSerial() {
        return serial;
    }

    public String getImage() {
        return image;
    }

    public int getAbsent() {
        return absent;
    }

}
