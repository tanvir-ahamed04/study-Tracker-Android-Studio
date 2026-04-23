package com.example.studytracker;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.UUID;

public class AddEditTaskActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etDescription, etSubject;
    private TextView selectedDateText, selectedTimeText;
    private MaterialButton btnDatePicker, btnTimePicker;
    private RadioGroup radioGroupPriority;
    private SwitchMaterial switchReminder;
    private NumberPicker numberPickerMinutes;
    private MaterialButton btnSave;
    private StorageHelper storageHelper;
    private ClassTask editingTask;
    private boolean isEditMode = false;

    private Calendar selectedCalendar = Calendar.getInstance();
    private String selectedDate = "";
    private String selectedTime = "";
    private int selectedHour = 0;
    private int selectedMinute = 0;
    private int selectedYear = 0;
    private int selectedMonth = 0;
    private int selectedDay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        storageHelper = new StorageHelper(this);
        initializeViews();
        setupNumberPicker();
        setupListeners();
        setupDateAndTimePickers();

        if (getIntent().hasExtra("task")) {
            isEditMode = true;
            editingTask = (ClassTask) getIntent().getSerializableExtra("task");
            loadTaskData();
        }
    }

    private void initializeViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etSubject = findViewById(R.id.etSubject);
        selectedDateText = findViewById(R.id.selectedDateText);
        selectedTimeText = findViewById(R.id.selectedTimeText);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnTimePicker = findViewById(R.id.btnTimePicker);
        radioGroupPriority = findViewById(R.id.radioGroupPriority);
        switchReminder = findViewById(R.id.switchReminder);
        numberPickerMinutes = findViewById(R.id.numberPickerMinutes);
        btnSave = findViewById(R.id.btnSave);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void setupNumberPicker() {
        numberPickerMinutes.setMinValue(5);
        numberPickerMinutes.setMaxValue(120);
        numberPickerMinutes.setValue(30);
        numberPickerMinutes.setWrapSelectorWheel(false);
    }

    private void setupDateAndTimePickers() {
        btnDatePicker.setOnClickListener(v -> showDatePicker());
        btnTimePicker.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    selectedYear = year1;
                    selectedMonth = month1;
                    selectedDay = dayOfMonth;
                    selectedDate = String.format("%d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    selectedDateText.setText("📅 Selected: " + selectedDate);
                    selectedDateText.setVisibility(View.VISIBLE);
                    btnDatePicker.setText("Date Selected");
                    btnDatePicker.setIconTintResource(android.R.color.holo_green_dark);

                    // Update calendar
                    selectedCalendar.set(Calendar.YEAR, year1);
                    selectedCalendar.set(Calendar.MONTH, month1);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minute;
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                    selectedTimeText.setText("⏰ Selected: " + selectedTime);
                    selectedTimeText.setVisibility(View.VISIBLE);
                    btnTimePicker.setText("Time Selected");
                    btnTimePicker.setIconTintResource(android.R.color.holo_green_dark);

                    // Update calendar
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedCalendar.set(Calendar.MINUTE, minute);
                    selectedCalendar.set(Calendar.SECOND, 0);
                }, selectedHour, selectedMinute, true);

        timePickerDialog.show();
    }

    private void setupListeners() {
        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            findViewById(R.id.reminderOptions).setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void loadTaskData() {
        etTitle.setText(editingTask.getTitle());
        etDescription.setText(editingTask.getDescription());
        etSubject.setText(editingTask.getSubject());

        selectedDate = editingTask.getDueDate();
        selectedTime = editingTask.getDueTime();

        if (selectedDate != null && !selectedDate.isEmpty()) {
            selectedDateText.setText("📅 Selected: " + selectedDate);
            selectedDateText.setVisibility(View.VISIBLE);
            btnDatePicker.setText("Date Selected");

            // Parse date
            String[] dateParts = selectedDate.split("-");
            if (dateParts.length == 3) {
                selectedYear = Integer.parseInt(dateParts[0]);
                selectedMonth = Integer.parseInt(dateParts[1]) - 1;
                selectedDay = Integer.parseInt(dateParts[2]);
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
            }
        }

        if (selectedTime != null && !selectedTime.isEmpty()) {
            selectedTimeText.setText("⏰ Selected: " + selectedTime);
            selectedTimeText.setVisibility(View.VISIBLE);
            btnTimePicker.setText("Time Selected");

            // Parse time
            String[] timeParts = selectedTime.split(":");
            if (timeParts.length == 2) {
                selectedHour = Integer.parseInt(timeParts[0]);
                selectedMinute = Integer.parseInt(timeParts[1]);
                selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                selectedCalendar.set(Calendar.MINUTE, selectedMinute);
            }
        }

        int priority = editingTask.getPriority();
        if (priority == 1) {
            radioGroupPriority.check(R.id.radioHigh);
        } else if (priority == 2) {
            radioGroupPriority.check(R.id.radioMedium);
        } else {
            radioGroupPriority.check(R.id.radioLow);
        }

        switchReminder.setChecked(editingTask.isReminderEnabled());
        numberPickerMinutes.setValue(editingTask.getReminderMinutes());

        findViewById(R.id.reminderOptions).setVisibility(
                editingTask.isReminderEnabled() ? View.VISIBLE : View.GONE);
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            return;
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a due time", Toast.LENGTH_SHORT).show();
            return;
        }

        String description = etDescription.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();

        int priority = 2;
        int selectedId = radioGroupPriority.getCheckedRadioButtonId();
        if (selectedId == R.id.radioHigh) {
            priority = 1;
        } else if (selectedId == R.id.radioLow) {
            priority = 3;
        }

        boolean reminderEnabled = switchReminder.isChecked();
        int reminderMinutes = numberPickerMinutes.getValue();

        if (isEditMode) {
            editingTask.setTitle(title);
            editingTask.setDescription(description);
            editingTask.setSubject(subject);
            editingTask.setDueDate(selectedDate);
            editingTask.setDueTime(selectedTime);
            editingTask.setPriority(priority);
            editingTask.setReminderEnabled(reminderEnabled);
            editingTask.setReminderMinutes(reminderMinutes);
            storageHelper.updateTask(editingTask);

            if (reminderEnabled) {
                scheduleAlarm(editingTask.getId(), title, subject, selectedDate, selectedTime, reminderMinutes);
            }

            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
        } else {
            String id = UUID.randomUUID().toString();
            ClassTask task = new ClassTask(id, title, description, subject, selectedDate, selectedTime,
                    priority, false, reminderEnabled, reminderMinutes);
            storageHelper.addTask(task);

            if (reminderEnabled) {
                scheduleAlarm(id, title, subject, selectedDate, selectedTime, reminderMinutes);
            }

            Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void scheduleAlarm(String taskId, String title, String subject, String date, String time, int reminderMinutes) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        String[] dateParts = date.split("-");
        String[] timeParts = time.split(":");

        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int day = Integer.parseInt(dateParts[2]);
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        calendar.set(year, month, day, hour, minute);
        calendar.add(Calendar.MINUTE, -reminderMinutes);

        if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
            Intent intent = new Intent(this, TaskReminderReceiver.class);
            intent.putExtra("taskTitle", title);
            intent.putExtra("subject", subject);
            intent.putExtra("dueDate", date);
            intent.putExtra("dueTime", time);
            intent.putExtra("taskId", taskId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, taskId.hashCode(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            Toast.makeText(this, "Alarm set for " + calendar.getTime().toString(), Toast.LENGTH_LONG).show();
        }
    }
}