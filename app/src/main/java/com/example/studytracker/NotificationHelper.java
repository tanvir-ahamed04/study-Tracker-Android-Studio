package com.example.studytracker;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;

public class NotificationHelper {
    private static final String CHANNEL_ID = "study_tracker_channel";
    private static final String CHANNEL_NAME = "Study Tracker";
    private static final String CHANNEL_DESCRIPTION = "Notifications for classes and tasks";

    private Context context;
    private NotificationManager notificationManager;
    private AlarmManager alarmManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showClassReminder(String className, String location, String roomNumber, String teacherName) {
        String content = "Class: " + className;
        if (location != null && !location.isEmpty()) content += "\nLocation: " + location;
        if (roomNumber != null && !roomNumber.isEmpty()) content += ", Room: " + roomNumber;
        if (teacherName != null && !teacherName.isEmpty()) content += "\nTeacher: " + teacherName;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_edit)
                .setContentTitle("📚 Class Reminder")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 200, 500});

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void showTaskReminder(String taskTitle, String subject, String dueDate, String dueTime) {
        String content = "Task: " + taskTitle;
        if (subject != null && !subject.isEmpty()) content += "\nSubject: " + subject;
        content += "\nDue: " + dueDate + " at " + dueTime;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_edit)
                .setContentTitle("⏰ Task Reminder")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 200, 500, 200, 500});

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void scheduleClassReminder(ClassSchedule schedule, String scheduleId) {
        if (!schedule.isReminderEnabled()) return;

        Calendar calendar = Calendar.getInstance();
        String[] timeParts = schedule.getStartTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute - schedule.getReminderMinutes());
        calendar.set(Calendar.SECOND, 0);

        // Set day of week
        int dayOfWeek = getDayOfWeekValue(schedule.getDayOfWeek());
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        Intent intent = new Intent(context, ClassReminderReceiver.class);
        intent.putExtra("className", schedule.getSubjectName());
        intent.putExtra("location", schedule.getLocation());
        intent.putExtra("roomNumber", schedule.getRoomNumber());
        intent.putExtra("teacherName", schedule.getTeacherName());
        intent.putExtra("scheduleId", scheduleId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, scheduleId.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
            );
        }
    }

    public void scheduleTaskReminder(ClassTask task, String taskId) {
        if (!task.isReminderEnabled()) return;

        Calendar calendar = Calendar.getInstance();
        String[] dateParts = task.getDueDate().split("-");
        String[] timeParts = task.getDueTime().split(":");

        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int day = Integer.parseInt(dateParts[2]);
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        calendar.set(year, month, day, hour, minute - task.getReminderMinutes(), 0);

        Intent intent = new Intent(context, TaskReminderReceiver.class);
        intent.putExtra("taskTitle", task.getTitle());
        intent.putExtra("subject", task.getSubject());
        intent.putExtra("dueDate", task.getDueDate());
        intent.putExtra("dueTime", task.getDueTime());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, taskId.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private int getDayOfWeekValue(String day) {
        switch (day) {
            case "Monday": return Calendar.MONDAY;
            case "Tuesday": return Calendar.TUESDAY;
            case "Wednesday": return Calendar.WEDNESDAY;
            case "Thursday": return Calendar.THURSDAY;
            case "Friday": return Calendar.FRIDAY;
            case "Saturday": return Calendar.SATURDAY;
            case "Sunday": return Calendar.SUNDAY;
            default: return Calendar.MONDAY;
        }
    }
}