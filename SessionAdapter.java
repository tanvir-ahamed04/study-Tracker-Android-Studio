package com.example.studytracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {
    private List<StudySession> sessions = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudySession session = sessions.get(position);
        holder.bind(session);

        // Animate item entrance
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.fade_in));
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public void setSessions(List<StudySession> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView subjectText, hoursText, dateText, difficultyText, noteText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.subjectText);
            hoursText = itemView.findViewById(R.id.hoursText);
            dateText = itemView.findViewById(R.id.dateText);
            difficultyText = itemView.findViewById(R.id.difficultyText);
            noteText = itemView.findViewById(R.id.noteText);
        }

        public void bind(StudySession session) {
            subjectText.setText(session.getSubject());
            hoursText.setText(session.getHours() + " hrs");
            dateText.setText("📅 " + session.getDate());

            // Set difficulty with color
            String difficulty = session.getDifficulty();
            difficultyText.setText(" " + difficulty + " ");

            switch (difficulty) {
                case "Easy":
                    difficultyText.setBackgroundResource(R.drawable.difficulty_badge_easy);
                    break;
                case "Medium":
                    difficultyText.setBackgroundResource(R.drawable.difficulty_badge_medium);
                    break;
                case "Hard":
                    difficultyText.setBackgroundResource(R.drawable.difficulty_badge_hard);
                    break;
            }

            // Show note if exists
            if (session.getNote() != null && !session.getNote().isEmpty()) {
                noteText.setText("📝 " + session.getNote());
                noteText.setVisibility(View.VISIBLE);
            } else {
                noteText.setVisibility(View.GONE);
            }
        }
    }
}