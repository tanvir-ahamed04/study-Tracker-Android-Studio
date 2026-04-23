package com.example.studytracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.List;

public class AddSessionActivity extends AppCompatActivity {
    private TextInputEditText etSubject, etNote;
    private Button btnDatePicker, btnSave, btnDecrementHour, btnIncrementHour;
    private TextView selectedDateText, tvHours, tvHoursWarning;
    private RadioGroup difficultyGroup;
    private AppDatabase database;

    private String selectedDate = "";
    private int selectedYear = 0, selectedMonth = 0, selectedDay = 0;
    private int hoursStudied = 1;
    private String selectedDifficulty = "Medium";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        database = AppDatabase.getInstance(this);
        initializeViews();
        setupDatePicker();
        setupHourPicker();
        setupListeners();
        animateViews();
        setDefaultDate();
    }

    private void initializeViews() {
        etSubject = findViewById(R.id.etSubject);
        etNote = findViewById(R.id.etNote);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnSave = findViewById(R.id.btnSave);
        btnDecrementHour = findViewById(R.id.btnDecrementHour);
        btnIncrementHour = findViewById(R.id.btnIncrementHour);
        selectedDateText = findViewById(R.id.selectedDateText);
        tvHours = findViewById(R.id.tvHours);
        tvHoursWarning = findViewById(R.id.tvHoursWarning);
        difficultyGroup = findViewById(R.id.difficultyGroup);

        findViewById(R.id.backButton).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        difficultyGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioEasy) {
                selectedDifficulty = "Easy";
            } else if (checkedId == R.id.radioMedium) {
                selectedDifficulty = "Medium";
            } else if (checkedId == R.id.radioHard) {
                selectedDifficulty = "Hard";
            }
        });
    }

    private void setDefaultDate() {
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
        selectedDateText.setText("📅 Selected: " + selectedDate);
        selectedDateText.setVisibility(View.VISIBLE);
        btnDatePicker.setText("Date: " + selectedDate);
    }

    private void setupDatePicker() {
        btnDatePicker.setOnClickListener(v -> {
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
                        btnDatePicker.setText("Date: " + selectedDate);
                    }, year, month, day);

            datePickerDialog.show();
        });
    }

    private void setupHourPicker() {
        tvHours.setText(String.valueOf(hoursStudied));

        btnDecrementHour.setOnClickListener(v -> {
            if (hoursStudied > 1) {
                hoursStudied--;
                tvHours.setText(String.valueOf(hoursStudied));
                tvHoursWarning.setVisibility(View.GONE);
            } else {
                tvHoursWarning.setText("Minimum 1 hour required");
                tvHoursWarning.setVisibility(View.VISIBLE);
            }
        });

        btnIncrementHour.setOnClickListener(v -> {
            if (hoursStudied < 24) {
                hoursStudied++;
                tvHours.setText(String.valueOf(hoursStudied));
                tvHoursWarning.setVisibility(View.GONE);
            } else {
                tvHoursWarning.setText("Maximum 24 hours per day");
                tvHoursWarning.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            saveSession();
        });
    }

    private void saveSession() {
        String subject = etSubject.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        // Validation 1: Subject name cannot be empty
        if (subject.isEmpty()) {
            etSubject.setError("Subject name is required");
            etSubject.requestFocus();
            Toast.makeText(this, "Please enter a subject name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation 2: Date must be selected
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation 3: Hours must be valid
        if (hoursStudied <= 0) {
            Toast.makeText(this, "Please enter valid hours (minimum 1)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation 4: Check for duplicate session (same subject and date)
        new Thread(() -> {
            List<StudySessionEntity> existingSessions = database.studySessionDao().getAllSessions();
            for (StudySessionEntity session : existingSessions) {
                if (session.getSubject().equalsIgnoreCase(subject) && session.getDate().equals(selectedDate)) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Duplicate session! You already studied " + subject + " on " + selectedDate, Toast.LENGTH_LONG).show();
                    });
                    return;
                }
            }

            // Save to database
            StudySessionEntity session = new StudySessionEntity(subject, selectedDate, hoursStudied, selectedDifficulty, note);
            database.studySessionDao().insert(session);

            runOnUiThread(() -> {
                Toast.makeText(this, "✓ Session saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }).start();
    }

    private void animateViews() {
        View title = findViewById(R.id.title);
        title.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        View formCard = findViewById(R.id.formCard);
        formCard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        btnSave.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
    }
}