package com.example.studytracker;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StorageHelper {
    private static final String PREF_NAME = "study_tracker_prefs";
    private static final String KEY_SESSIONS = "study_sessions";
    private static final String KEY_SCHEDULES = "class_schedules";
    private static final String KEY_TASKS = "class_tasks";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public StorageHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Study Session methods
    public void saveSessions(List<StudySession> sessions) {
        String json = gson.toJson(sessions);
        sharedPreferences.edit().putString(KEY_SESSIONS, json).apply();
    }

    public List<StudySession> loadSessions() {
        String json = sharedPreferences.getString(KEY_SESSIONS, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<StudySession>>(){}.getType();
        return gson.fromJson(json, type);
    }

    // Class Schedule methods
    public void saveSchedules(List<ClassSchedule> schedules) {
        String json = gson.toJson(schedules);
        sharedPreferences.edit().putString(KEY_SCHEDULES, json).apply();
    }

    public List<ClassSchedule> loadSchedules() {
        String json = sharedPreferences.getString(KEY_SCHEDULES, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<ClassSchedule>>(){}.getType();
        List<ClassSchedule> schedules = gson.fromJson(json, type);
        return schedules != null ? schedules : new ArrayList<>();
    }

    public void addSchedule(ClassSchedule schedule) {
        List<ClassSchedule> schedules = loadSchedules();
        schedules.add(schedule);
        saveSchedules(schedules);
    }

    public void updateSchedule(ClassSchedule schedule) {
        List<ClassSchedule> schedules = loadSchedules();
        for (int i = 0; i < schedules.size(); i++) {
            if (schedules.get(i).getId().equals(schedule.getId())) {
                schedules.set(i, schedule);
                break;
            }
        }
        saveSchedules(schedules);
    }

    public void deleteSchedule(String scheduleId) {
        List<ClassSchedule> schedules = loadSchedules();
        schedules.removeIf(schedule -> schedule.getId().equals(scheduleId));
        saveSchedules(schedules);
    }

    public List<ClassSchedule> getSchedulesByDay(String dayOfWeek) {
        List<ClassSchedule> allSchedules = loadSchedules();
        List<ClassSchedule> daySchedules = new ArrayList<>();
        for (ClassSchedule schedule : allSchedules) {
            if (schedule.getDayOfWeek().equals(dayOfWeek)) {
                daySchedules.add(schedule);
            }
        }
        return daySchedules;
    }

    // Task methods
    public void saveTasks(List<ClassTask> tasks) {
        String json = gson.toJson(tasks);
        sharedPreferences.edit().putString(KEY_TASKS, json).apply();
    }

    public List<ClassTask> loadTasks() {
        String json = sharedPreferences.getString(KEY_TASKS, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<ClassTask>>(){}.getType();
        List<ClassTask> tasks = gson.fromJson(json, type);
        return tasks != null ? tasks : new ArrayList<>();
    }

    public void addTask(ClassTask task) {
        List<ClassTask> tasks = loadTasks();
        tasks.add(task);
        saveTasks(tasks);
    }

    public void updateTask(ClassTask task) {
        List<ClassTask> tasks = loadTasks();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(task.getId())) {
                tasks.set(i, task);
                break;
            }
        }
        saveTasks(tasks);
    }

    public void deleteTask(String taskId) {
        List<ClassTask> tasks = loadTasks();
        tasks.removeIf(task -> task.getId().equals(taskId));
        saveTasks(tasks);
    }

    public List<ClassTask> getPendingTasks() {
        List<ClassTask> allTasks = loadTasks();
        List<ClassTask> pendingTasks = new ArrayList<>();
        for (ClassTask task : allTasks) {
            if (!task.isCompleted()) {
                pendingTasks.add(task);
            }
        }
        return pendingTasks;
    }

    public List<ClassTask> getTasksBySubject(String subject) {
        List<ClassTask> allTasks = loadTasks();
        List<ClassTask> subjectTasks = new ArrayList<>();
        for (ClassTask task : allTasks) {
            if (task.getSubject().equalsIgnoreCase(subject)) {
                subjectTasks.add(task);
            }
        }
        return subjectTasks;
    }

    public List<ClassTask> getTasksByPriority(int priority) {
        List<ClassTask> allTasks = loadTasks();
        List<ClassTask> priorityTasks = new ArrayList<>();
        for (ClassTask task : allTasks) {
            if (task.getPriority() == priority) {
                priorityTasks.add(task);
            }
        }
        return priorityTasks;
    }

    // Utility methods
    public void clearAllData() {
        sharedPreferences.edit()
                .remove(KEY_SESSIONS)
                .remove(KEY_SCHEDULES)
                .remove(KEY_TASKS)
                .apply();
    }

    public int getTotalSchedulesCount() {
        return loadSchedules().size();
    }

    public int getTotalTasksCount() {
        return loadTasks().size();
    }

    public int getPendingTasksCount() {
        return getPendingTasks().size();
    }

    public int getCompletedTasksCount() {
        List<ClassTask> allTasks = loadTasks();
        int completed = 0;
        for (ClassTask task : allTasks) {
            if (task.isCompleted()) {
                completed++;
            }
        }
        return completed;
    }
}