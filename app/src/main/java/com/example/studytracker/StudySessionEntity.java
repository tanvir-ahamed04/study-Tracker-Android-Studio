package com.example.studytracker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "study_sessions")
public class StudySessionEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String subject;
    private String date;
    private double hours;
    private String difficulty;
    private String note;
    private long timestamp;

    public StudySessionEntity(String subject, String date, double hours, String difficulty, String note) {
        this.subject = subject;
        this.date = date;
        this.hours = hours;
        this.difficulty = difficulty;
        this.note = note;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public long getId() { return id; }
    public String getSubject() { return subject; }
    public String getDate() { return date; }
    public double getHours() { return hours; }
    public String getDifficulty() { return difficulty; }
    public String getNote() { return note; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setDate(String date) { this.date = date; }
    public void setHours(double hours) { this.hours = hours; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setNote(String note) { this.note = note; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}