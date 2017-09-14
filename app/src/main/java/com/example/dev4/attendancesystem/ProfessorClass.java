package com.example.dev4.attendancesystem;

import java.util.ArrayList;

/**
 * Created by Jasmin Gutierrez on 29/06/2017.
 */

public class ProfessorClass {
    private String id;
    private String name;
    private String email;
    private String dept;
    private String image;
    private String user;
    private String pass;
    public static ArrayList<ProfessorClass> CURRENT_LOGON = new ArrayList<>();

    public ProfessorClass(String id, String name, String email, String dept, String image, String user, String pass) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dept = dept;
        this.image = image;
        this.user = user;
        this.pass = pass;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDept() {
        return dept;
    }

    public String getImage() {
        return image;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
}
