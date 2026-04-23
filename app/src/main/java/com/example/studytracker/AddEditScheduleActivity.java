package com.example.studytracker;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.UUID;

public class AddEditScheduleActivity extends AppCompatActivity {
    private TextInputEditText etSubjectName, etLocation, etRoomNumber, etTeacherName, etNotes;
    private ChipGroup dayChipGroup;
    private Chip chipMonday, chipTuesday, chipWednesday, chipThursday, chipFriday, chipSaturday, chipSunday;
    private MaterialButton btnStartTime, btnEndTime, btnSave;
    private TextView selectedStartTimeText, selectedEndTimeText;
    private SwitchMaterial switchReminder;
    private NumberPicker numberPickerMinutes;
    private StorageHelper storageHelper;
    private ClassSchedule editingSchedule;
    private boolean isEditMode = false;

    private String selectedDay = "Monday";
    private String selectedStartTime = "";
    private String selectedEndTime = "";
    private int startHour = 0, startMinute = 0;
    private int endHour = 0, endMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_schedule);

        storageHelper = new StorageHelper(this);
        initializeViews();
        setupChipListeners();
        setupTimePickers();
        setupNumberPicker();
        setupListeners();

        if (getIntent().hasExtra("schedule")) {
            isEditMode = true;
            editingSchedule = (ClassSchedule) getIntent().getSerializableExtra("schedule");
            loadScheduleData();
        }
    }

    private void initializeViews() {
        etSubjectName = findViewById(R.id.etSubjectName);
        etLocation = findViewById(R.id.etLocation);
        etRoomNumber = findViewById(R.id.etRoomNumber);
        etTeacherName = findViewById(R.id.etTeacherName);
        etNotes = findViewById(R.id.etNotes);

        dayChipGroup = findViewById(R.id.dayChipGroup);
        chipMonday = findViewById(R.id.chipMonday);
        chipTuesday = findViewById(R.id.chipTuesday);
        chipWednesday = findViewById(R.id.chipWednesday);
        chipThursday = findViewById(R.id.chipThursday);
        chipFriday = findViewById(R.id.chipFriday);
        chipSaturday = findViewById(R.id.chipSaturday);
        chipSunday = findViewById(R.id.chipSunday);

        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnSave = findViewById(R.id.btnSave);

        selectedStartTimeText = findViewById(R.id.selectedStartTimeText);
        selectedEndTimeText = findViewById(R.id.selectedEndTimeText);

        switchReminder = findViewById(R.id.switchReminder);
        numberPickerMinutes = findViewById(R.id.numberPickerMinutes);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void setupChipListeners() {
        View.OnClickListener chipClickListener = v -> {
            Chip selectedChip = (Chip) v;
            updateChipSelection(selectedChip);
            selectedDay = selectedChip.getText().toString();
            // Convert short day to full day name
            switch (selectedDay) {
                case "Mon": selectedDay = "Monday"; break;
                case "Tue": selectedDay = "Tuesday"; break;
                case "Wed": selectedDay = "Wednesday"; break;
                case "Thu": selectedDay = "Thursday"; break;
                case "Fri": selectedDay = "Friday"; break;
                case "Sat": selectedDay = "Saturday"; break;
                case "Sun": selectedDay = "Sunday"; break;
            }
        };

        chipMonday.setOnClickListener(chipClickListener);
        chipTuesday.setOnClickListener(chipClickListener);
        chipWednesday.setOnClickListener(chipClickListener);
        chipThursday.setOnClickListener(chipClickListener);
        chipFriday.setOnClickListener(chipClickListener);
        chipSaturday.setOnClickListener(chipClickListener);
        chipSunday.setOnClickListener(chipClickListener);
    }

    private void updateChipSelection(Chip selectedChip) {
        Chip[] chips = {chipMonday, chipTuesday, chipWednesday, chipThursday, chipFriday, chipSaturday, chipSunday};
        for (Chip chip : chips) {
            if (chip == selectedChip) {
                chip.setChecked(true);
                chip.setChipBackgroundColorResource(R.color.primary);
                chip.setTextColor(getColor(R.color.white));
            } else {
                chip.setChecked(false);
                chip.setChipBackgroundColorResource(R.color.chip_unselected);
                chip.setTextColor(getColor(R.color.text_primary));
            }
        }
    }

    private void setupTimePickers() {
        btnStartTime.setOnClickListener(v -> showTimePicker(true));
        btnEndTime.setOnClickListener(v -> showTimePicker(false));
    }

    private void showTimePicker(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = isStartTime ? startHour : endHour;
        int minute = isStartTime ? startMinute : endMinute;

        if (hour == 0 && minute == 0 && !isStartTime && !selectedEndTime.isEmpty()) {
            hour = 9;
            minute = 0;
        } else if (hour == 0 && minute == 0 && isStartTime && !selectedStartTime.isEmpty()) {
            String[] parts = selectedStartTime.split(":");
            if (parts.length == 2) {
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            }
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    if (isStartTime) {
                        selectedStartTime = time;
                        startHour = hourOfDay;
                        startMinute = minuteOfHour;
                        btnStartTime.setText("Start: " + time);
                        btnStartTime.setIconTintResource(android.R.color.holo_green_dark);
                        selectedStartTimeText.setText("Start Time: " + time);
                        selectedStartTimeText.setVisibility(View.VISIBLE);
                    } else {
                        selectedEndTime = time;
                        endHour = hourOfDay;
                        endMinute = minuteOfHour;
                        btnEndTime.setText("End: " + time);
                        btnEndTime.setIconTintResource(android.R.color.holo_green_dark);
                        selectedEndTimeText.setText("End Time: " + time);
                        selectedEndTimeText.setVisibility(View.VISIBLE);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void setupNumberPicker() {
        numberPickerMinutes.setMinValue(5);
        numberPickerMinutes.setMaxValue(60);
        numberPickerMinutes.setValue(15);
        numberPickerMinutes.setWrapSelectorWheel(false);
    }

    private void setupListeners() {
        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            findViewById(R.id.reminderOptions).setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        btnSave.setOnClickListener(v -> saveSchedule());
    }

    private void loadScheduleData() {
        etSubjectName.setText(editingSchedule.getSubjectName());
        etLocation.setText(editingSchedule.getLocation());
        etRoomNumber.setText(editingSchedule.getRoomNumber());
        etTeacherName.setText(editingSchedule.getTeacherName());
        etNotes.setText(editingSchedule.getNotes());

        selectedDay = editingSchedule.getDayOfWeek();
        selectedStartTime = editingSchedule.getStartTime();
        selectedEndTime = editingSchedule.getEndTime();

        // Set day chip
        String shortDay = selectedDay.substring(0, 3);
        Chip targetChip = null;
        switch (shortDay) {
            case "Mon": targetChip = chipMonday; break;
            case "Tue": targetChip = chipTuesday; break;
            case "Wed": targetChip = chipWednesday; break;
            case "Thu": targetChip = chipThursday; break;
            case "Fri": targetChip = chipFriday; break;
            case "Sat": targetChip = chipSaturday; break;
            case "Sun": targetChip = chipSunday; break;
        }
        if (targetChip != null) updateChipSelection(targetChip);

        // Set time buttons
        if (!selectedStartTime.isEmpty()) {
            btnStartTime.setText("Start: " + selectedStartTime);
            selectedStartTimeText.setText("Start Time: " + selectedStartTime);
            selectedStartTimeText.setVisibility(View.VISIBLE);
            String[] startParts = selectedStartTime.split(":");
            if (startParts.length == 2) {
                startHour = Integer.parseInt(startParts[0]);
                startMinute = Integer.parseInt(startParts[1]);
            }
        }

        if (!selectedEndTime.isEmpty()) {
            btnEndTime.setText("End: " + selectedEndTime);
            selectedEndTimeText.setText("End Time: " + selectedEndTime);
            selectedEndTimeText.setVisibility(View.VISIBLE);
            String[] endParts = selectedEndTime.split(":");
            if (endParts.length == 2) {
                endHour = Integer.parseInt(endParts[0]);
                endMinute = Integer.parseInt(endParts[1]);
            }
        }

        switchReminder.setChecked(editingSchedule.isReminderEnabled());
        numberPickerMinutes.setValue(editingSchedule.getReminderMinutes());

        findViewById(R.id.reminderOptions).setVisibility(
                editingSchedule.isReminderEnabled() ? View.VISIBLE : View.GONE);
    }

    private void saveSchedule() {
        String subjectName = etSubjectName.getText().toString().trim();

        if (subjectName.isEmpty()) {
            etSubjectName.setError("Subject name is required");
            return;
        }

        if (selectedStartTime.isEmpty()) {
            Toast.makeText(this, "Please select start time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedEndTime.isEmpty()) {
            Toast.makeText(this, "Please select end time", Toast.LENGTH_SHORT).show();
            return;
        }

        String location = etLocation.getText().toString().trim();
        String roomNumber = etRoomNumber.getText().toString().trim();
        String teacherName = etTeacherName.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        boolean reminderEnabled = switchReminder.isChecked();
        int reminderMinutes = numberPickerMinutes.getValue();

        if (isEditMode) {
            editingSchedule.setSubjectName(subjectName);
            editingSchedule.setDayOfWeek(selectedDay);
            editingSchedule.setStartTime(selectedStartTime);
            editingSchedule.setEndTime(selectedEndTime);
            editingSchedule.setLocation(location);
            editingSchedule.setRoomNumber(roomNumber);
            editingSchedule.setTeacherName(teacherName);
            editingSchedule.setNotes(notes);
            editingSchedule.setReminderEnabled(reminderEnabled);
            editingSchedule.setReminderMinutes(reminderMinutes);
            storageHelper.updateSchedule(editingSchedule);
            Toast.makeText(this, "Schedule updated", Toast.LENGTH_SHORT).show();
        } else {
            String id = UUID.randomUUID().toString();
            ClassSchedule schedule = new ClassSchedule(id, subjectName, selectedDay, selectedStartTime, selectedEndTime,
                    location, roomNumber, teacherName, notes, reminderEnabled, reminderMinutes);
            storageHelper.addSchedule(schedule);
            Toast.makeText(this, "Schedule saved", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}