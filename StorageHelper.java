package com.example.studytracker;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StorageHelper {
    private static final String PREF_NAME = "study_tracker_prefs";
    private static final String KEY_SESSIONS = "study_sessions";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public StorageHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveSessions(List<StudySession> sessions) {
        String json = gson.toJson(sessions);
        sharedPreferences.edit().putString(KEY_SESSIONS, json).apply();
    }

    public List<StudySession> loadSessions() {
        String json = sharedPreferences.getString(KEY_SESSIONS, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<StudySession>>(){}.getType();
        return gson.fromJson(json, type);
    }
}