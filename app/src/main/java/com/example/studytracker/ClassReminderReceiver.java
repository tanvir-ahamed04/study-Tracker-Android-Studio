package com.example.studytracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ClassReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String className = intent.getStringExtra("className");
        String location = intent.getStringExtra("location");
        String roomNumber = intent.getStringExtra("roomNumber");
        String teacherName = intent.getStringExtra("teacherName");

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.showClassReminder(className, location, roomNumber, teacherName);
    }
}