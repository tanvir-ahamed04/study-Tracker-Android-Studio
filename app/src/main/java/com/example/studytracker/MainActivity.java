package com.example.studytracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {
    // Dashboard Stats
    private TextView totalHoursText, totalSessionsText, averageHoursText, mostStudiedText;
    private TextView productivityScore, streakCount, consistencyRate, bestSubjectText;

    // Analytics Insights
    private TextView hardestSubjectText, avgPerDayText, topPerformingDayText, totalSubjectsText;
    private TextView goalProgressText, todayHoursText;

    // Recent Sessions Section
    private TextView sessionCountText, weekCountText, monthCountText, totalHoursMiniText;
    private MaterialButton viewAllButton;

    // UI Components
    private RecyclerView recyclerView;
    private SessionAdapter adapter;
    private AppDatabase database;
    private List<StudySessionEntity> sessions;
    private View progressBar;

    // Goal settings (2 hours per day default)
    private double dailyGoal = 2.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getInstance(this);

        initializeViews();
        setupClickListeners();
        animateViews();
        loadData();
    }

    private void initializeViews() {
        // Dashboard Stats
        totalHoursText = findViewById(R.id.totalHours);
        totalSessionsText = findViewById(R.id.totalSessions);
        averageHoursText = findViewById(R.id.averageHours);
        mostStudiedText = findViewById(R.id.mostStudied);
        productivityScore = findViewById(R.id.productivityScore);
        streakCount = findViewById(R.id.streakCount);
        consistencyRate = findViewById(R.id.consistencyRate);
        bestSubjectText = findViewById(R.id.bestSubjectText);
        hardestSubjectText = findViewById(R.id.hardestSubjectText);
        avgPerDayText = findViewById(R.id.avgPerDayText);
        topPerformingDayText = findViewById(R.id.topPerformingDayText);
        totalSubjectsText = findViewById(R.id.totalSubjectsText);
        goalProgressText = findViewById(R.id.goalProgressText);
        todayHoursText = findViewById(R.id.todayHoursText);
        progressBar = findViewById(R.id.progressBar);

        // Recent Sessions Section Views
        sessionCountText = findViewById(R.id.sessionCount);
        weekCountText = findViewById(R.id.weekCount);
        monthCountText = findViewById(R.id.monthCount);
        totalHoursMiniText = findViewById(R.id.totalHoursMini);
        viewAllButton = findViewById(R.id.viewAllButton);

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SessionAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        new Thread(() -> {
            sessions = database.studySessionDao().getAllSessions();
            runOnUiThread(() -> {
                // Show only today's sessions in recent sessions
                updateRecentSessionsList();
                updateStats();
                updateRecentStats();
            });
        }).start();
    }

    private void updateRecentSessionsList() {
        // Get today's date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());

        // Filter sessions for today only
        List<StudySession> todaySessions = new ArrayList<>();
        for (StudySessionEntity entity : sessions) {
            if (entity.getDate().equals(todayDate)) {
                todaySessions.add(new StudySession(
                        entity.getSubject(), entity.getDate(), entity.getHours(),
                        entity.getDifficulty(), entity.getNote()
                ));
            }
        }

        // Show only today's sessions (max 5 for recent view)
        int maxDisplay = Math.min(todaySessions.size(), 5);
        List<StudySession> recentSessions = todaySessions.subList(0, maxDisplay);
        adapter.setSessions(recentSessions);

        // Update session count text
        if (sessionCountText != null) {
            sessionCountText.setText(String.valueOf(todaySessions.size()));
        }
    }

    private void updateStats() {
        new Thread(() -> {
            double totalHours = database.studySessionDao().getTotalHours();
            int sessionCount = database.studySessionDao().getSessionCount();
            int uniqueDays = database.studySessionDao().getUniqueDaysCount();

            // Calculate today's hours
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = sdf.format(new Date());
            double todayHours = database.studySessionDao().getHoursByDate(today);

            // Calculate productivity = today_hours / daily_goal * 100
            double productivity = (todayHours / dailyGoal) * 100;
            if (productivity > 100) productivity = 100;
            if (productivity < 0) productivity = 0;

            // Calculate streak (consecutive days)
            int streak = calculateStreak();

            // Calculate consistency = (unique_days / 30) * 100
            int consistency = (int) ((uniqueDays / 30.0) * 100);
            if (consistency > 100) consistency = 100;
            if (consistency < 0) consistency = 0;

            // Get most studied subject
            List<String> subjects = database.studySessionDao().getAllSubjects();
            String mostStudied = "-";
            int maxCount = 0;
            for (String subject : subjects) {
                int count = 0;
                for (StudySessionEntity session : sessions) {
                    if (session.getSubject().equals(subject)) count++;
                }
                if (count > maxCount) {
                    maxCount = count;
                    mostStudied = subject;
                }
            }

            double average = sessionCount > 0 ? totalHours / sessionCount : 0;

            final double finalTotalHours = totalHours;
            final int finalSessionCount = sessionCount;
            final double finalAverage = average;
            final String finalMostStudied = mostStudied;
            final double finalProductivity = productivity;
            final int finalStreak = streak;
            final int finalConsistency = consistency;
            final double finalTodayHours = todayHours;

            runOnUiThread(() -> {
                totalHoursText.setText(String.format("%.1f", finalTotalHours));
                totalSessionsText.setText(String.valueOf(finalSessionCount));
                averageHoursText.setText(String.format("%.1f", finalAverage));
                mostStudiedText.setText(finalMostStudied);
                productivityScore.setText(String.format("%.0f%%", finalProductivity));
                streakCount.setText(String.valueOf(finalStreak));
                consistencyRate.setText(finalConsistency + "%");
                todayHoursText.setText(String.format("%.1f / %.1f hrs", finalTodayHours, dailyGoal));
                goalProgressText.setText(String.format("%.0f%%", finalProductivity));

                // Update progress bar width and color based on goal completion
                updateProgressBar(finalProductivity, finalTodayHours);

                // Calculate additional insights
                calculateInsights();
                animateStatsUpdate();
            });
        }).start();
    }

    private void updateRecentStats() {
        new Thread(() -> {
            // Calculate this week's sessions count
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            String weekStart = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

            int weekCount = 0;
            int monthCount = 0;
            double totalHours = 0;

            // Get current month start
            String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

            for (StudySessionEntity session : sessions) {
                totalHours += session.getHours();

                // Count this week
                if (session.getDate().compareTo(weekStart) >= 0) {
                    weekCount++;
                }

                // Count this month
                if (session.getDate().startsWith(currentMonth)) {
                    monthCount++;
                }
            }

            final int finalWeekCount = weekCount;
            final int finalMonthCount = monthCount;
            final double finalTotalHours = totalHours;

            runOnUiThread(() -> {
                if (weekCountText != null) {
                    weekCountText.setText(String.valueOf(finalWeekCount));
                }
                if (monthCountText != null) {
                    monthCountText.setText(String.valueOf(finalMonthCount));
                }
                if (totalHoursMiniText != null) {
                    totalHoursMiniText.setText(String.format("%.1f", finalTotalHours));
                }
            });
        }).start();
    }

    private void updateProgressBar(double productivity, double todayHours) {
        if (progressBar != null) {
            CardView parentCard = findViewById(R.id.cardProductivity);
            if (parentCard != null) {
                parentCard.post(() -> {
                    int parentWidth = parentCard.getWidth() - 100;
                    if (parentWidth > 0) {
                        int progressWidth = (int) ((productivity / 100) * parentWidth);
                        ViewGroup.LayoutParams params = progressBar.getLayoutParams();
                        params.width = Math.max(progressWidth, 20);
                        progressBar.setLayoutParams(params);

                        // Change progress bar color based on goal completion
                        if (todayHours >= dailyGoal) {
                            // Goal completed - Green color
                            progressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.success));
                        } else {
                            // Goal not completed - Original color (white)
                            progressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        }
                    }
                });
            }
        }
    }

    private int calculateStreak() {
        if (sessions.isEmpty()) return 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        int streak = 0;

        for (int i = 0; i < 30; i++) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            String date = sdf.format(calendar.getTime());

            boolean hasSession = false;
            for (StudySessionEntity session : sessions) {
                if (session.getDate().equals(date)) {
                    hasSession = true;
                    break;
                }
            }

            if (hasSession) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    private void calculateInsights() {
        new Thread(() -> {
            // Hardest subject based on difficulty
            Map<String, Double> subjectDifficulty = new HashMap<>();
            Map<String, Integer> subjectCount = new HashMap<>();

            for (StudySessionEntity session : sessions) {
                double difficultyScore = 0;
                switch (session.getDifficulty()) {
                    case "Hard": difficultyScore = 3; break;
                    case "Medium": difficultyScore = 2; break;
                    case "Easy": difficultyScore = 1; break;
                }
                subjectDifficulty.put(session.getSubject(),
                        subjectDifficulty.getOrDefault(session.getSubject(), 0.0) + difficultyScore);
                subjectCount.put(session.getSubject(),
                        subjectCount.getOrDefault(session.getSubject(), 0) + 1);
            }

            String hardestSubject = "";
            double maxDifficulty = 0;
            for (Map.Entry<String, Double> entry : subjectDifficulty.entrySet()) {
                double avgDifficulty = entry.getValue() / subjectCount.get(entry.getKey());
                if (avgDifficulty > maxDifficulty) {
                    maxDifficulty = avgDifficulty;
                    hardestSubject = entry.getKey();
                }
            }

            // Best subject (most studied)
            String bestSubject = "";
            int maxCount = 0;
            for (Map.Entry<String, Integer> entry : subjectCount.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    bestSubject = entry.getKey();
                }
            }

            // Average hours per day
            int uniqueDays = database.studySessionDao().getUniqueDaysCount();
            double totalHours = database.studySessionDao().getTotalHours();
            double avgPerDay = uniqueDays > 0 ? totalHours / uniqueDays : 0;

            // Best day of week
            Map<String, Double> dayHours = new HashMap<>();
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            for (StudySessionEntity session : sessions) {
                try {
                    Date date = dateFormat.parse(session.getDate());
                    String dayName = dayFormat.format(date);
                    dayHours.put(dayName, dayHours.getOrDefault(dayName, 0.0) + session.getHours());
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            }

            String topDay = "";
            double maxDayHours = 0;
            for (Map.Entry<String, Double> entry : dayHours.entrySet()) {
                if (entry.getValue() > maxDayHours) {
                    maxDayHours = entry.getValue();
                    topDay = entry.getKey();
                }
            }

            int totalSubjects = subjectCount.size();

            String finalHardestSubject = hardestSubject.isEmpty() ? "N/A" : hardestSubject;
            String finalBestSubject = bestSubject.isEmpty() ? "N/A" : bestSubject;
            double finalAvgPerDay = avgPerDay;
            String finalTopDay = topDay.isEmpty() ? "N/A" : topDay;
            int finalTotalSubjects = totalSubjects;

            runOnUiThread(() -> {
                hardestSubjectText.setText(finalHardestSubject);
                bestSubjectText.setText(finalBestSubject);
                avgPerDayText.setText(String.format("%.1f hrs", finalAvgPerDay));
                topPerformingDayText.setText(finalTopDay);
                totalSubjectsText.setText(String.valueOf(finalTotalSubjects));
            });
        }).start();
    }

    private void setupClickListeners() {
        MaterialButton btnAddSession = findViewById(R.id.btnAddSession);
        MaterialButton btnHistory = findViewById(R.id.btnHistory);
        MaterialButton btnClassRoutine = findViewById(R.id.btnClassRoutine);
        MaterialButton btnTasks = findViewById(R.id.btnTasks);
        MaterialButton btnAnalytics = findViewById(R.id.btnAnalytics);

        btnAddSession.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            Intent intent = new Intent(MainActivity.this, AddSessionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnHistory.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnClassRoutine.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            Intent intent = new Intent(MainActivity.this, RoutineActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnTasks.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            Intent intent = new Intent(MainActivity.this, TaskActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnAnalytics.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            Intent intent = new Intent(MainActivity.this, AnalyticsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // View All Button Click Listener
        if (viewAllButton != null) {
            viewAllButton.setOnClickListener(v -> {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }

    private void animateViews() {
        View title = findViewById(R.id.titleHeader);
        View subtitle = findViewById(R.id.subtitle);

        title.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        subtitle.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeIn.setStartOffset(200);
        findViewById(R.id.cardTotalHours).startAnimation(fadeIn);

        Animation fadeIn2 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeIn2.setStartOffset(300);
        findViewById(R.id.cardTotalSessions).startAnimation(fadeIn2);

        Animation fadeIn3 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeIn3.setStartOffset(400);
        findViewById(R.id.cardAverage).startAnimation(fadeIn3);

        Animation fadeIn4 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeIn4.setStartOffset(500);
        findViewById(R.id.cardMostStudied).startAnimation(fadeIn4);

        Animation fadeIn5 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeIn5.setStartOffset(600);
        findViewById(R.id.cardProductivity).startAnimation(fadeIn5);
    }

    private void animateStatsUpdate() {
        totalHoursText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        totalSessionsText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        averageHoursText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        mostStudiedText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        productivityScore.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        streakCount.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        consistencyRate.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && progressBar != null && goalProgressText != null) {
            String progressText = goalProgressText.getText().toString().replace("%", "");
            try {
                double progress = Double.parseDouble(progressText);
                // Get today's hours to determine color
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String today = sdf.format(new Date());
                new Thread(() -> {
                    double todayHours = database.studySessionDao().getHoursByDate(today);
                    runOnUiThread(() -> updateProgressBar(progress, todayHours));
                }).start();
            } catch (NumberFormatException e) {
                // Ignore parsing error
            }
        }
    }
}