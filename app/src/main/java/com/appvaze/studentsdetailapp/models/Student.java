package com.appvaze.studentsdetailapp.models;

public class Student {

    String stdId, stdName, surName, fatherName, nationalId, dob, gender;
    boolean isLocal;

    public Student(String stdId, String stdName, String surName, String fatherName, String nationalId, String dob, String gender, boolean isLocal) {
        this.stdId = stdId;
        this.stdName = stdName;
        this.surName = surName;
        this.fatherName = fatherName;
        this.nationalId = nationalId;
        this.dob = dob;
        this.gender = gender;
        this.isLocal = isLocal;
    }

    public String getStdId() {
        return stdId;
    }

    public void setStdId(String stdId) {
        this.stdId = stdId;
    }

    public String getStdName() {
        return stdName;
    }

    public void setStdName(String stdName) {
        this.stdName = stdName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }
}
