package com.example.studytracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoutineActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RoutineAdapter adapter;
    private StorageHelper storageHelper;
    private ChipGroup dayChipGroup;
    private Chip chipMonday, chipTuesday, chipWednesday, chipThursday, chipFriday, chipSaturday, chipSunday;
    private MaterialButton btnAddSchedule;
    private ImageView backButton;
    private TextView totalClassesText, teachersCountText, locationsCountText, scheduleCountText;
    private LinearLayout emptyState;
    private MaterialButton btnGoToAddClass;
    private List<ClassSchedule> schedules;
    private String selectedDay = "Monday";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        storageHelper = new StorageHelper(this);
        initializeViews();
        setupChipListeners();
        setupClickListeners();
        loadSchedules();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.routineRecyclerView);
        dayChipGroup = findViewById(R.id.dayChipGroup);
        chipMonday = findViewById(R.id.chipMonday);
        chipTuesday = findViewById(R.id.chipTuesday);
        chipWednesday = findViewById(R.id.chipWednesday);
        chipThursday = findViewById(R.id.chipThursday);
        chipFriday = findViewById(R.id.chipFriday);
        chipSaturday = findViewById(R.id.chipSaturday);
        chipSunday = findViewById(R.id.chipSunday);
        btnAddSchedule = findViewById(R.id.btnAddSchedule);
        backButton = findViewById(R.id.backButton);
        totalClassesText = findViewById(R.id.totalClassesText);
        teachersCountText = findViewById(R.id.teachersCountText);
        locationsCountText = findViewById(R.id.locationsCountText);
        scheduleCountText = findViewById(R.id.scheduleCountText);
        emptyState = findViewById(R.id.emptyState);
        btnGoToAddClass = findViewById(R.id.btnGoToAddClass);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoutineAdapter(new ArrayList<>(), schedule -> {
            Intent intent = new Intent(RoutineActivity.this, AddEditScheduleActivity.class);
            intent.putExtra("schedule", schedule);
            intent.putExtra("isEdit", true);
            startActivity(intent);
        }, scheduleId -> {
            storageHelper.deleteSchedule(scheduleId);
            loadSchedules();
            Toast.makeText(this, "Schedule deleted", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupChipListeners() {
        chipMonday.setOnClickListener(v -> {
            updateChipSelection(chipMonday);
            selectedDay = "Monday";
            loadSchedules();
        });
        chipTuesday.setOnClickListener(v -> {
            updateChipSelection(chipTuesday);
            selectedDay = "Tuesday";
            loadSchedules();
        });
        chipWednesday.setOnClickListener(v -> {
            updateChipSelection(chipWednesday);
            selectedDay = "Wednesday";
            loadSchedules();
        });
        chipThursday.setOnClickListener(v -> {
            updateChipSelection(chipThursday);
            selectedDay = "Thursday";
            loadSchedules();
        });
        chipFriday.setOnClickListener(v -> {
            updateChipSelection(chipFriday);
            selectedDay = "Friday";
            loadSchedules();
        });
        chipSaturday.setOnClickListener(v -> {
            updateChipSelection(chipSaturday);
            selectedDay = "Saturday";
            loadSchedules();
        });
        chipSunday.setOnClickListener(v -> {
            updateChipSelection(chipSunday);
            selectedDay = "Sunday";
            loadSchedules();
        });
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

    private void setupClickListeners() {
        btnAddSchedule.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            Intent intent = new Intent(RoutineActivity.this, AddEditScheduleActivity.class);
            intent.putExtra("isEdit", false);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnGoToAddClass.setOnClickListener(v -> {
            Intent intent = new Intent(RoutineActivity.this, AddEditScheduleActivity.class);
            intent.putExtra("isEdit", false);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        backButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void loadSchedules() {
        schedules = storageHelper.getSchedulesByDay(selectedDay);
        adapter.updateSchedules(schedules);
        updateStats(schedules);

        if (schedules.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void updateStats(List<ClassSchedule> schedules) {
        int totalClasses = schedules.size();
        Set<String> teachers = new HashSet<>();
        Set<String> locations = new HashSet<>();

        for (ClassSchedule schedule : schedules) {
            if (schedule.getTeacherName() != null && !schedule.getTeacherName().isEmpty()) {
                teachers.add(schedule.getTeacherName());
            }
            if (schedule.getLocation() != null && !schedule.getLocation().isEmpty()) {
                locations.add(schedule.getLocation());
            }
        }

        totalClassesText.setText(String.valueOf(totalClasses));
        teachersCountText.setText(String.valueOf(teachers.size()));
        locationsCountText.setText(String.valueOf(locations.size()));
        scheduleCountText.setText(totalClasses + " classes");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchedules();
    }
}