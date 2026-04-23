package com.example.studytracker;

import java.io.Serializable;

public class ClassTask implements Serializable {
    private String id;
    private String title;
    private String description;
    private String subject;
    private String dueDate;
    private String dueTime;
    private int priority; // 1=High, 2=Medium, 3=Low
    private boolean isCompleted;
    private boolean reminderEnabled;
    private int reminderMinutes;

    public ClassTask(String id, String title, String description, String subject,
                     String dueDate, String dueTime, int priority, boolean isCompleted,
                     boolean reminderEnabled, int reminderMinutes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.priority = priority;
        this.isCompleted = isCompleted;
        this.reminderEnabled = reminderEnabled;
        this.reminderMinutes = reminderMinutes;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSubject() { return subject; }
    public String getDueDate() { return dueDate; }
    public String getDueTime() { return dueTime; }
    public int getPriority() { return priority; }
    public boolean isCompleted() { return isCompleted; }
    public boolean isReminderEnabled() { return reminderEnabled; }
    public int getReminderMinutes() { return reminderMinutes; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setDueTime(String dueTime) { this.dueTime = dueTime; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public void setReminderEnabled(boolean reminderEnabled) { this.reminderEnabled = reminderEnabled; }
    public void setReminderMinutes(int reminderMinutes) { this.reminderMinutes = reminderMinutes; }
}