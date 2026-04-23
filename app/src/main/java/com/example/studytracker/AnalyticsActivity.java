package com.example.studytracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AnalyticsActivity extends AppCompatActivity {
    private ImageView backButton;
    private CardView cardWeeklyChart, cardSubjectChart, cardStats;
    private BarChart weeklyBarChart;
    private PieChart subjectPieChart;
    private ChipGroup chartChipGroup;
    private Chip chipWeekly, chipSubject;
    private MaterialButton btnRefresh;

    // Stats TextViews
    private TextView totalStudyTimeText, avgPerDayText, hardestSubjectText, topSubjectText;
    private TextView weeklyTotalText, totalSubjectsText, bestDayText;

    private StorageHelper storageHelper;
    private List<StudySession> sessions;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        storageHelper = new StorageHelper(this);
        initializeViews();
        setupClickListeners();
        loadData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        cardWeeklyChart = findViewById(R.id.cardWeeklyChart);
        cardSubjectChart = findViewById(R.id.cardSubjectChart);
        cardStats = findViewById(R.id.cardStats);
        weeklyBarChart = findViewById(R.id.weeklyBarChart);
        subjectPieChart = findViewById(R.id.subjectPieChart);
        chartChipGroup = findViewById(R.id.chartChipGroup);
        chipWeekly = findViewById(R.id.chipWeekly);
        chipSubject = findViewById(R.id.chipSubject);
        btnRefresh = findViewById(R.id.btnRefresh);

        totalStudyTimeText = findViewById(R.id.totalStudyTimeText);
        avgPerDayText = findViewById(R.id.avgPerDayText);
        hardestSubjectText = findViewById(R.id.hardestSubjectText);
        topSubjectText = findViewById(R.id.topSubjectText);
        weeklyTotalText = findViewById(R.id.weeklyTotalText);
        totalSubjectsText = findViewById(R.id.totalSubjectsText);
        bestDayText = findViewById(R.id.bestDayText);

        // Initially show weekly chart
        cardWeeklyChart.setVisibility(View.VISIBLE);
        cardSubjectChart.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnRefresh.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            loadData();
        });

        chipWeekly.setOnClickListener(v -> {
            updateChipSelection(chipWeekly);
            cardWeeklyChart.setVisibility(View.VISIBLE);
            cardSubjectChart.setVisibility(View.GONE);
            setupWeeklyChart();
        });

        chipSubject.setOnClickListener(v -> {
            updateChipSelection(chipSubject);
            cardWeeklyChart.setVisibility(View.GONE);
            cardSubjectChart.setVisibility(View.VISIBLE);
            setupSubjectPieChart();
        });
    }

    private void updateChipSelection(Chip selectedChip) {
        Chip[] chips = {chipWeekly, chipSubject};
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

    private void loadData() {
        sessions = storageHelper.loadSessions();
        updateStats();
        setupWeeklyChart();
        setupSubjectPieChart();
    }

    private void updateStats() {
        if (sessions.isEmpty()) {
            totalStudyTimeText.setText("0 hrs");
            avgPerDayText.setText("0 hrs");
            hardestSubjectText.setText("N/A");
            topSubjectText.setText("N/A");
            weeklyTotalText.setText("0 hrs");
            totalSubjectsText.setText("0");
            bestDayText.setText("N/A");
            return;
        }

        // Total study time per subject
        Map<String, Double> subjectHours = new HashMap<>();
        Map<String, Double> subjectDifficulty = new HashMap<>();
        Map<String, Integer> subjectCount = new HashMap<>();
        Set<String> uniqueDates = new HashSet<>();
        double totalHours = 0;

        for (StudySession session : sessions) {
            totalHours += session.getHours();
            subjectHours.put(session.getSubject(),
                    subjectHours.getOrDefault(session.getSubject(), 0.0) + session.getHours());
            uniqueDates.add(session.getDate());
            subjectCount.put(session.getSubject(),
                    subjectCount.getOrDefault(session.getSubject(), 0) + 1);

            double difficultyScore = 0;
            switch (session.getDifficulty()) {
                case "Hard": difficultyScore = 3; break;
                case "Medium": difficultyScore = 2; break;
                case "Easy": difficultyScore = 1; break;
            }
            subjectDifficulty.put(session.getSubject(),
                    subjectDifficulty.getOrDefault(session.getSubject(), 0.0) + difficultyScore);
        }

        // Total study time
        totalStudyTimeText.setText(String.format("%.1f hrs", totalHours));

        // Average hours per day
        double avgPerDay = uniqueDates.isEmpty() ? 0 : totalHours / uniqueDates.size();
        avgPerDayText.setText(String.format("%.1f hrs", avgPerDay));

        // Hardest subject based on difficulty
        String hardestSubject = "";
        double maxDifficulty = 0;
        for (Map.Entry<String, Double> entry : subjectDifficulty.entrySet()) {
            double avgDifficulty = entry.getValue() / subjectCount.get(entry.getKey());
            if (avgDifficulty > maxDifficulty) {
                maxDifficulty = avgDifficulty;
                hardestSubject = entry.getKey();
            }
        }
        hardestSubjectText.setText(hardestSubject.isEmpty() ? "N/A" : hardestSubject);

        // Top subject (most studied)
        String topSubject = "";
        double maxHours = 0;
        for (Map.Entry<String, Double> entry : subjectHours.entrySet()) {
            if (entry.getValue() > maxHours) {
                maxHours = entry.getValue();
                topSubject = entry.getKey();
            }
        }
        topSubjectText.setText(topSubject.isEmpty() ? "N/A" : topSubject);

        // Total subjects
        totalSubjectsText.setText(String.valueOf(subjectHours.size()));

        // Best day
        Map<String, Double> dayHours = new HashMap<>();
        for (StudySession session : sessions) {
            try {
                Date date = dateFormat.parse(session.getDate());
                String dayName = dayFormat.format(date);
                dayHours.put(dayName, dayHours.getOrDefault(dayName, 0.0) + session.getHours());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String bestDay = "";
        double bestDayHours = 0;
        for (Map.Entry<String, Double> entry : dayHours.entrySet()) {
            if (entry.getValue() > bestDayHours) {
                bestDayHours = entry.getValue();
                bestDay = entry.getKey();
            }
        }
        bestDayText.setText(bestDay.isEmpty() ? "N/A" : bestDay);

        // Weekly total (last 7 days)
        double weeklyTotal = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date sevenDaysAgo = calendar.getTime();
        for (StudySession session : sessions) {
            try {
                Date sessionDate = dateFormat.parse(session.getDate());
                if (sessionDate.after(sevenDaysAgo)) {
                    weeklyTotal += session.getHours();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        weeklyTotalText.setText(String.format("%.1f hrs", weeklyTotal));
    }

    private void setupWeeklyChart() {
        if (sessions.isEmpty()) {
            weeklyBarChart.setNoDataText("No data available");
            weeklyBarChart.invalidate();
            return;
        }

        Map<String, Double> weeklyHours = new HashMap<>();
        Calendar calendar = Calendar.getInstance();

        // Get last 7 days
        for (int i = 6; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            String dayName = dayFormat.format(calendar.getTime());
            weeklyHours.put(dayName, 0.0);
        }

        // Calculate hours per day
        for (StudySession session : sessions) {
            try {
                Date sessionDate = dateFormat.parse(session.getDate());
                calendar.setTime(sessionDate);
                String dayName = dayFormat.format(sessionDate);
                if (weeklyHours.containsKey(dayName)) {
                    weeklyHours.put(dayName, weeklyHours.get(dayName) + session.getHours());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Prepare chart data
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Double> entry : weeklyHours.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Study Hours");
        dataSet.setColor(getColor(R.color.primary));
        dataSet.setValueTextColor(getColor(R.color.text_primary));
        dataSet.setValueTextSize(11f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1f", value);
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        weeklyBarChart.setData(barData);
        weeklyBarChart.getDescription().setEnabled(false);
        weeklyBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        weeklyBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        weeklyBarChart.getXAxis().setGranularity(1f);
        weeklyBarChart.getXAxis().setLabelCount(labels.size());
        weeklyBarChart.getAxisLeft().setAxisMinimum(0f);
        weeklyBarChart.getAxisRight().setEnabled(false);
        weeklyBarChart.getLegend().setEnabled(false);
        weeklyBarChart.animateY(800);
        weeklyBarChart.invalidate();
    }

    private void setupSubjectPieChart() {
        if (sessions.isEmpty()) {
            subjectPieChart.setNoDataText("No data available");
            subjectPieChart.invalidate();
            return;
        }

        Map<String, Double> subjectHours = new HashMap<>();
        double totalHours = 0;

        for (StudySession session : sessions) {
            subjectHours.put(session.getSubject(),
                    subjectHours.getOrDefault(session.getSubject(), 0.0) + session.getHours());
            totalHours += session.getHours();
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        int[] colors = {
                getColor(R.color.primary),
                getColor(R.color.accent),
                getColor(R.color.success),
                getColor(R.color.warning),
                getColor(R.color.error),
                getColor(R.color.primary_dark),
                getColor(R.color.teal_200)
        };

        int colorIndex = 0;
        for (Map.Entry<String, Double> entry : subjectHours.entrySet()) {
            double percentage = (entry.getValue() / totalHours) * 100;
            entries.add(new PieEntry(entry.getValue().floatValue(),
                    entry.getKey() + " (" + String.format("%.1f", percentage) + "%)"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Subjects");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(getColor(R.color.text_primary));
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1f", value);
            }
        });

        PieData pieData = new PieData(dataSet);
        subjectPieChart.setData(pieData);
        subjectPieChart.getDescription().setEnabled(false);
        subjectPieChart.setCenterText("Total\n" + String.format("%.1f", totalHours) + " hrs");
        subjectPieChart.setCenterTextSize(12f);
        subjectPieChart.setCenterTextColor(getColor(R.color.text_primary));
        subjectPieChart.setUsePercentValues(false);
        subjectPieChart.setDrawHoleEnabled(true);
        subjectPieChart.setHoleRadius(40f);
        subjectPieChart.setTransparentCircleRadius(45f);
        subjectPieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        subjectPieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        subjectPieChart.getLegend().setTextSize(10f);
        subjectPieChart.animateY(800);
        subjectPieChart.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}