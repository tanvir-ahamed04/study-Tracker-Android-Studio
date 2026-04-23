package com.example.studytracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TaskReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle = intent.getStringExtra("taskTitle");
        String subject = intent.getStringExtra("subject");
        String dueDate = intent.getStringExtra("dueDate");
        String dueTime = intent.getStringExtra("dueTime");

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.showTaskReminder(taskTitle, subject, dueDate, dueTime);
    }
}