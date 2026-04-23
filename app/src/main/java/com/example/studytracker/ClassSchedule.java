package com.example.studytracker;

import java.io.Serializable;

public class ClassSchedule implements Serializable {
    private String id;
    private String subjectName;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String location;
    private String roomNumber;
    private String teacherName;
    private String notes;
    private boolean reminderEnabled;
    private int reminderMinutes;

    public ClassSchedule(String id, String subjectName, String dayOfWeek, String startTime,
                         String endTime, String location, String roomNumber,
                         String teacherName, String notes, boolean reminderEnabled, int reminderMinutes) {
        this.id = id;
        this.subjectName = subjectName;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.roomNumber = roomNumber;
        this.teacherName = teacherName;
        this.notes = notes;
        this.reminderEnabled = reminderEnabled;
        this.reminderMinutes = reminderMinutes;
    }

    // Getters
    public String getId() { return id; }
    public String getSubjectName() { return subjectName; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public String getRoomNumber() { return roomNumber; }
    public String getTeacherName() { return teacherName; }
    public String getNotes() { return notes; }
    public boolean isReminderEnabled() { return reminderEnabled; }
    public int getReminderMinutes() { return reminderMinutes; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setLocation(String location) { this.location = location; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setReminderEnabled(boolean reminderEnabled) { this.reminderEnabled = reminderEnabled; }
    public void setReminderMinutes(int reminderMinutes) { this.reminderMinutes = reminderMinutes; }
}