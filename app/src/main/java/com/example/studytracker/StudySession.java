package com.example.studytracker;

import java.io.Serializable;

public class StudySession implements Serializable {
    private String subject;
    private String date;
    private double hours;
    private String difficulty;
    private String note;

    public StudySession(String subject, String date, double hours, String difficulty, String note) {
        this.subject = subject;
        this.date = date;
        this.hours = hours;
        this.difficulty = difficulty;
        this.note = note;
    }

    public String getSubject() { return subject; }
    public String getDate() { return date; }
    public double getHours() { return hours; }
    public String getDifficulty() { return difficulty; }
    public String getNote() { return note; }

    public void setSubject(String subject) { this.subject = subject; }
    public void setDate(String date) { this.date = date; }
    public void setHours(double hours) { this.hours = hours; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setNote(String note) { this.note = note; }
}