package com.example.studytracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<ClassTask> tasks;
    private OnTaskEditListener editListener;
    private OnTaskDeleteListener deleteListener;
    private OnTaskCompleteListener completeListener;


    public interface OnTaskEditListener {
        void onEdit(ClassTask task);
    }

    public interface OnTaskDeleteListener {
        void onDelete(String taskId);
    }

    public interface OnTaskCompleteListener {
        void onComplete(ClassTask task);
    }

    public TaskAdapter(List<ClassTask> tasks,
                       OnTaskEditListener editListener,
                       OnTaskDeleteListener deleteListener,
                       OnTaskCompleteListener completeListener) {
        this.tasks = tasks;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
        this.completeListener = completeListener;
    }

    public void updateTasks(List<ClassTask> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClassTask task = tasks.get(position);
        holder.bind(task);

        holder.checkComplete.setOnClickListener(v -> {
            completeListener.onComplete(task);
        });

        holder.btnEdit.setOnClickListener(v -> editListener.onEdit(task));

        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(task.getId()));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkComplete;
        private TextView titleText, subjectText, descriptionText, dueDateText, dueTimeText, priorityBadge;
        private ImageButton btnEdit, btnDelete;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            checkComplete = itemView.findViewById(R.id.checkComplete);
            titleText = itemView.findViewById(R.id.titleText);
            subjectText = itemView.findViewById(R.id.subjectText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            dueDateText = itemView.findViewById(R.id.dueDateText);
            dueTimeText = itemView.findViewById(R.id.dueTimeText);
            priorityBadge = itemView.findViewById(R.id.priorityBadge);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cardView = itemView.findViewById(R.id.cardView);
        }

        public void bind(ClassTask task) {
            titleText.setText(task.getTitle());

            if (task.getSubject() != null && !task.getSubject().isEmpty()) {
                subjectText.setText("📚 " + task.getSubject());
                subjectText.setVisibility(View.VISIBLE);
            } else {
                subjectText.setVisibility(View.GONE);
            }

            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                descriptionText.setText(task.getDescription());
                descriptionText.setVisibility(View.VISIBLE);
            } else {
                descriptionText.setVisibility(View.GONE);
            }

            dueDateText.setText("📅 " + task.getDueDate());
            dueTimeText.setText("⏰ " + task.getDueTime());

            // Set priority badge
            String priorityText;
            int priorityColor;
            switch (task.getPriority()) {
                case 1:
                    priorityText = "HIGH PRIORITY";
                    priorityColor = 0xFFEF4444;
                    break;
                case 2:
                    priorityText = "MEDIUM PRIORITY";
                    priorityColor = 0xFFF59E0B;
                    break;
                default:
                    priorityText = "LOW PRIORITY";
                    priorityColor = 0xFF10B981;
                    break;
            }
            priorityBadge.setText(priorityText);
            priorityBadge.setBackgroundColor(priorityColor);

            // Set check status
            checkComplete.setChecked(task.isCompleted());

            // Style based on completion
            if (task.isCompleted()) {
                titleText.setAlpha(0.5f);
                cardView.setAlpha(0.7f);
            } else {
                titleText.setAlpha(1f);
                cardView.setAlpha(1f);
            }
        }
    }
}