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
import java.util.List;

public class TaskActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private StorageHelper storageHelper;
    private MaterialButton btnAddTask;
    private ChipGroup chipGroup;
    private Chip chipAll, chipPending, chipCompleted, chipHigh, chipMedium, chipLow;
    private ImageView backButton;
    private TextView pendingTasksCount, completedTasksCount, totalTasksCount, taskListCount;
    private LinearLayout emptyState;
    private MaterialButton btnGoToAddTask;
    private List<ClassTask> allTasks;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        storageHelper = new StorageHelper(this);
        initializeViews();
        setupClickListeners();
        loadTasks();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.taskRecyclerView);
        btnAddTask = findViewById(R.id.btnAddTask);
        chipGroup = findViewById(R.id.chipGroup);
        chipAll = findViewById(R.id.chipAll);
        chipPending = findViewById(R.id.chipPending);
        chipCompleted = findViewById(R.id.chipCompleted);
        chipHigh = findViewById(R.id.chipHigh);
        chipMedium = findViewById(R.id.chipMedium);
        chipLow = findViewById(R.id.chipLow);
        backButton = findViewById(R.id.backButton);
        pendingTasksCount = findViewById(R.id.pendingTasksCount);
        completedTasksCount = findViewById(R.id.completedTasksCount);
        totalTasksCount = findViewById(R.id.totalTasksCount);
        taskListCount = findViewById(R.id.taskListCount);
        emptyState = findViewById(R.id.emptyState);
        btnGoToAddTask = findViewById(R.id.btnGoToAddTask);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(new ArrayList<>(), task -> {
            Intent intent = new Intent(TaskActivity.this, AddEditTaskActivity.class);
            intent.putExtra("task", task);
            intent.putExtra("isEdit", true);
            startActivity(intent);
        }, taskId -> {
            storageHelper.deleteTask(taskId);
            loadTasks();
            Toast.makeText(TaskActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
        }, task -> {
            task.setCompleted(!task.isCompleted());
            storageHelper.updateTask(task);
            loadTasks();
            String status = task.isCompleted() ? "completed" : "pending";
            Toast.makeText(TaskActivity.this, "Task marked as " + status, Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnAddTask.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            Intent intent = new Intent(TaskActivity.this, AddEditTaskActivity.class);
            intent.putExtra("isEdit", false);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnGoToAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(TaskActivity.this, AddEditTaskActivity.class);
            intent.putExtra("isEdit", false);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        backButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        chipAll.setOnClickListener(v -> {
            currentFilter = "all";
            updateChipSelection(chipAll);
            filterTasks();
        });

        chipPending.setOnClickListener(v -> {
            currentFilter = "pending";
            updateChipSelection(chipPending);
            filterTasks();
        });

        chipCompleted.setOnClickListener(v -> {
            currentFilter = "completed";
            updateChipSelection(chipCompleted);
            filterTasks();
        });

        chipHigh.setOnClickListener(v -> {
            currentFilter = "high";
            updateChipSelection(chipHigh);
            filterTasks();
        });

        chipMedium.setOnClickListener(v -> {
            currentFilter = "medium";
            updateChipSelection(chipMedium);
            filterTasks();
        });

        chipLow.setOnClickListener(v -> {
            currentFilter = "low";
            updateChipSelection(chipLow);
            filterTasks();
        });
    }

    private void updateChipSelection(Chip selectedChip) {
        Chip[] chips = {chipAll, chipPending, chipCompleted, chipHigh, chipMedium, chipLow};
        for (Chip chip : chips) {
            chip.setChecked(chip == selectedChip);
        }
    }

    private void loadTasks() {
        allTasks = storageHelper.loadTasks();
        updateTaskCounts();
        filterTasks();

        // Show/hide empty state
        if (allTasks.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void updateTaskCounts() {
        int pending = 0;
        int completed = 0;

        for (ClassTask task : allTasks) {
            if (task.isCompleted()) {
                completed++;
            } else {
                pending++;
            }
        }

        pendingTasksCount.setText(String.valueOf(pending));
        completedTasksCount.setText(String.valueOf(completed));
        totalTasksCount.setText(String.valueOf(allTasks.size()));
        taskListCount.setText(allTasks.size() + " tasks");
    }

    private void filterTasks() {
        List<ClassTask> filteredTasks = new ArrayList<>();

        switch (currentFilter) {
            case "pending":
                for (ClassTask task : allTasks) {
                    if (!task.isCompleted()) filteredTasks.add(task);
                }
                break;
            case "completed":
                for (ClassTask task : allTasks) {
                    if (task.isCompleted()) filteredTasks.add(task);
                }
                break;
            case "high":
                for (ClassTask task : allTasks) {
                    if (task.getPriority() == 1) filteredTasks.add(task);
                }
                break;
            case "medium":
                for (ClassTask task : allTasks) {
                    if (task.getPriority() == 2) filteredTasks.add(task);
                }
                break;
            case "low":
                for (ClassTask task : allTasks) {
                    if (task.getPriority() == 3) filteredTasks.add(task);
                }
                break;
            default:
                filteredTasks = allTasks;
                break;
        }

        adapter.updateTasks(filteredTasks);

        // Update filter count display
        String filterText = "";
        switch (currentFilter) {
            case "pending":
                filterText = "Pending";
                break;
            case "completed":
                filterText = "Completed";
                break;
            case "high":
                filterText = "High Priority";
                break;
            case "medium":
                filterText = "Medium Priority";
                break;
            case "low":
                filterText = "Low Priority";
                break;
            default:
                filterText = "All";
                break;
        }
        taskListCount.setText(filteredTasks.size() + " " + filterText.toLowerCase() + " tasks");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }
}