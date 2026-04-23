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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SessionAdapter adapter;
    private AppDatabase database;
    private ImageView backButton;
    private TextView totalSessionsCount, totalHoursCount, averageHoursCount, sessionCountText;
    private LinearLayout emptyState;
    private MaterialButton btnGoToAdd;
    private ChipGroup dayFilterChipGroup, weekdayFilterChipGroup;
    private Chip chipAllDays, chipToday, chipYesterday, chipThisWeek, chipThisMonth;
    private Chip chipMonday, chipTuesday, chipWednesday, chipThursday, chipFriday, chipSaturday, chipSunday;

    private List<StudySessionEntity> allSessions;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        database = AppDatabase.getInstance(this);

        initializeViews();
        setupFilterListeners();
        setupClickListeners();
        loadSessions();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.historyRecyclerView);
        backButton = findViewById(R.id.backButton);
        totalSessionsCount = findViewById(R.id.totalSessionsCount);
        totalHoursCount = findViewById(R.id.totalHoursCount);
        averageHoursCount = findViewById(R.id.averageHoursCount);
        sessionCountText = findViewById(R.id.sessionCountText);
        emptyState = findViewById(R.id.emptyState);
        btnGoToAdd = findViewById(R.id.btnGoToAdd);

        dayFilterChipGroup = findViewById(R.id.dayFilterChipGroup);
        weekdayFilterChipGroup = findViewById(R.id.weekdayFilterChipGroup);

        chipAllDays = findViewById(R.id.chipAllDays);
        chipToday = findViewById(R.id.chipToday);
        chipYesterday = findViewById(R.id.chipYesterday);
        chipThisWeek = findViewById(R.id.chipThisWeek);
        chipThisMonth = findViewById(R.id.chipThisMonth);

        chipMonday = findViewById(R.id.chipMonday);
        chipTuesday = findViewById(R.id.chipTuesday);
        chipWednesday = findViewById(R.id.chipWednesday);
        chipThursday = findViewById(R.id.chipThursday);
        chipFriday = findViewById(R.id.chipFriday);
        chipSaturday = findViewById(R.id.chipSaturday);
        chipSunday = findViewById(R.id.chipSunday);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SessionAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void setupFilterListeners() {
        // Day filter listeners
        chipAllDays.setOnClickListener(v -> {
            updateDayChipSelection(chipAllDays);
            currentFilter = "all";
            filterSessions();
        });

        chipToday.setOnClickListener(v -> {
            updateDayChipSelection(chipToday);
            currentFilter = "today";
            filterSessions();
        });

        chipYesterday.setOnClickListener(v -> {
            updateDayChipSelection(chipYesterday);
            currentFilter = "yesterday";
            filterSessions();
        });

        chipThisWeek.setOnClickListener(v -> {
            updateDayChipSelection(chipThisWeek);
            currentFilter = "week";
            filterSessions();
        });

        chipThisMonth.setOnClickListener(v -> {
            updateDayChipSelection(chipThisMonth);
            currentFilter = "month";
            filterSessions();
        });

        // Weekday filter listeners
        View.OnClickListener weekdayListener = v -> {
            Chip selectedChip = (Chip) v;
            clearWeekdaySelections();
            selectedChip.setChecked(true);
            selectedChip.setChipBackgroundColorResource(R.color.primary);
            selectedChip.setTextColor(getColor(R.color.white));
            currentFilter = selectedChip.getText().toString();
            filterSessions();
        };

        chipMonday.setOnClickListener(weekdayListener);
        chipTuesday.setOnClickListener(weekdayListener);
        chipWednesday.setOnClickListener(weekdayListener);
        chipThursday.setOnClickListener(weekdayListener);
        chipFriday.setOnClickListener(weekdayListener);
        chipSaturday.setOnClickListener(weekdayListener);
        chipSunday.setOnClickListener(weekdayListener);
    }

    private void updateDayChipSelection(Chip selectedChip) {
        Chip[] chips = {chipAllDays, chipToday, chipYesterday, chipThisWeek, chipThisMonth};
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
        clearWeekdaySelections();
    }

    private void clearWeekdaySelections() {
        Chip[] weekdays = {chipMonday, chipTuesday, chipWednesday, chipThursday, chipFriday, chipSaturday, chipSunday};
        for (Chip chip : weekdays) {
            chip.setChecked(false);
            chip.setChipBackgroundColorResource(R.color.chip_unselected);
            chip.setTextColor(getColor(R.color.text_primary));
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnGoToAdd.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, AddSessionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void loadSessions() {
        new Thread(() -> {
            allSessions = database.studySessionDao().getAllSessions();
            runOnUiThread(() -> {
                filterSessions();
            });
        }).start();
    }

    private void filterSessions() {
        new Thread(() -> {
            List<StudySessionEntity> filteredSessions = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String todayDate = sdf.format(new Date());

            for (StudySessionEntity session : allSessions) {
                boolean include = false;

                switch (currentFilter) {
                    case "today":
                        if (session.getDate().equals(todayDate)) include = true;
                        break;
                    case "yesterday":
                        calendar.setTime(new Date());
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        String yesterdayDate = sdf.format(calendar.getTime());
                        if (session.getDate().equals(yesterdayDate)) include = true;
                        break;
                    case "week":
                        calendar.setTime(new Date());
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        String weekStart = sdf.format(calendar.getTime());
                        calendar.add(Calendar.DAY_OF_YEAR, 6);
                        String weekEnd = sdf.format(calendar.getTime());
                        if (session.getDate().compareTo(weekStart) >= 0 && session.getDate().compareTo(weekEnd) <= 0) include = true;
                        break;
                    case "month":
                        String currentMonth = todayDate.substring(0, 7);
                        if (session.getDate().startsWith(currentMonth)) include = true;
                        break;
                    case "Monday":
                    case "Tuesday":
                    case "Wednesday":
                    case "Thursday":
                    case "Friday":
                    case "Saturday":
                    case "Sunday":
                        String dayOfWeek = getDayOfWeek(session.getDate());
                        if (dayOfWeek.equals(currentFilter)) include = true;
                        break;
                    default:
                        include = true;
                        break;
                }

                if (include) filteredSessions.add(session);
            }

            // Convert to old model for adapter
            List<StudySession> oldSessions = new ArrayList<>();
            for (StudySessionEntity entity : filteredSessions) {
                oldSessions.add(new StudySession(
                        entity.getSubject(), entity.getDate(), entity.getHours(),
                        entity.getDifficulty(), entity.getNote()
                ));
            }

            runOnUiThread(() -> {
                adapter.setSessions(oldSessions);
                updateStats(oldSessions);

                if (oldSessions.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyState.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    private String getDayOfWeek(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        try {
            Date date = sdf.parse(dateString);
            return dayFormat.format(date);
        } catch (ParseException e) {
            return "";
        }
    }

    private void updateStats(List<StudySession> sessions) {
        int totalSessions = sessions.size();
        double totalHours = 0;

        for (StudySession session : sessions) {
            totalHours += session.getHours();
        }

        double averageHours = totalSessions > 0 ? totalHours / totalSessions : 0;

        totalSessionsCount.setText(String.valueOf(totalSessions));
        totalHoursCount.setText(String.format("%.1f", totalHours));
        averageHoursCount.setText(String.format("%.1f", averageHours));
        sessionCountText.setText(totalSessions + " items");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSessions();
    }
}