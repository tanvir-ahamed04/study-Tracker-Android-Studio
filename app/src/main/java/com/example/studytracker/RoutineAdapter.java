package com.example.studytracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.ViewHolder> {
    private List<ClassSchedule> schedules;
    private OnScheduleClickListener editListener;
    private OnScheduleDeleteListener deleteListener;

    public interface OnScheduleClickListener {
        void onEdit(ClassSchedule schedule);
    }

    public interface OnScheduleDeleteListener {
        void onDelete(String scheduleId);
    }

    public RoutineAdapter(List<ClassSchedule> schedules,
                          OnScheduleClickListener editListener,
                          OnScheduleDeleteListener deleteListener) {
        this.schedules = schedules;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    public void updateSchedules(List<ClassSchedule> newSchedules) {
        this.schedules = newSchedules;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClassSchedule schedule = schedules.get(position);
        holder.bind(schedule);

        holder.btnEdit.setOnClickListener(v -> editListener.onEdit(schedule));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(schedule.getId()));
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView subjectText, timeText, locationText, teacherText;
        private ImageButton btnEdit, btnDelete;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.subjectText);
            timeText = itemView.findViewById(R.id.timeText);
            locationText = itemView.findViewById(R.id.locationText);
            teacherText = itemView.findViewById(R.id.teacherText);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cardView = itemView.findViewById(R.id.cardView);
        }

        public void bind(ClassSchedule schedule) {
            subjectText.setText(schedule.getSubjectName());
            timeText.setText(schedule.getStartTime() + " - " + schedule.getEndTime());

            if (schedule.getLocation() != null && !schedule.getLocation().isEmpty()) {
                locationText.setText("📍 " + schedule.getLocation());
                locationText.setVisibility(View.VISIBLE);
            } else {
                locationText.setVisibility(View.GONE);
            }

            if (schedule.getTeacherName() != null && !schedule.getTeacherName().isEmpty()) {
                teacherText.setText("👨‍🏫 " + schedule.getTeacherName());
                teacherText.setVisibility(View.VISIBLE);
            } else {
                teacherText.setVisibility(View.GONE);
            }
        }
    }
}