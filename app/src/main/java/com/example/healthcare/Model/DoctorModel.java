package com.example.healthcare.Model;

public class DoctorModel {
    private String doctorName;
    private String doctorSpecialization;
    private String doctorExperience;
    private String doctorLocation;
    private String doctorFees;
    private String img_url;
    private String date;
    private String time;
    private String username;
    private String doctorId;
    private String phoneNumber; // Added phone number field

    public DoctorModel() {
    }

    public DoctorModel(String doctorName, String doctorSpecialization, String doctorExperience, String doctorLocation, String doctorFees, String img_url, String date, String time, String username, String doctorId, String phoneNumber) {
        this.doctorName = doctorName;
        this.doctorSpecialization = doctorSpecialization;
        this.doctorExperience = doctorExperience;
        this.doctorLocation = doctorLocation;
        this.doctorFees = doctorFees;
        this.img_url = img_url;
        this.date = date;
        this.time = time;
        this.username = username;
        this.doctorId = doctorId;
        this.phoneNumber = phoneNumber;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public void setDoctorSpecialization(String doctorSpecialization) {
        this.doctorSpecialization = doctorSpecialization;
    }

    public String getDoctorExperience() {
        return doctorExperience;
    }

    public void setDoctorExperience(String doctorExperience) {
        this.doctorExperience = doctorExperience;
    }

    public String getDoctorLocation() {
        return doctorLocation;
    }

    public void setDoctorLocation(String doctorLocation) {
        this.doctorLocation = doctorLocation;
    }

    public String getDoctorFees() {
        return doctorFees;
    }

    public void setDoctorFees(String doctorFees) {
        this.doctorFees = doctorFees;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
