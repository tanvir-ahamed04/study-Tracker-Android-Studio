package com.example.studytracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface StudySessionDao {
    @Insert
    long insert(StudySessionEntity session);

    @Update
    void update(StudySessionEntity session);

    @Delete
    void delete(StudySessionEntity session);

    @Query("SELECT * FROM study_sessions ORDER BY date DESC")
    List<StudySessionEntity> getAllSessions();

    @Query("SELECT * FROM study_sessions WHERE subject LIKE :search OR note LIKE :search ORDER BY date DESC")
    List<StudySessionEntity> searchSessions(String search);

    @Query("SELECT * FROM study_sessions WHERE subject = :subject ORDER BY date DESC")
    List<StudySessionEntity> getSessionsBySubject(String subject);

    @Query("SELECT * FROM study_sessions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<StudySessionEntity> getSessionsByDateRange(String startDate, String endDate);

    @Query("SELECT DISTINCT subject FROM study_sessions ORDER BY subject")
    List<String> getAllSubjects();

    @Query("SELECT SUM(hours) FROM study_sessions")
    double getTotalHours();

    @Query("SELECT SUM(hours) FROM study_sessions WHERE date = :date")
    double getHoursByDate(String date);

    @Query("SELECT COUNT(*) FROM study_sessions")
    int getSessionCount();

    @Query("SELECT COUNT(DISTINCT date) FROM study_sessions")
    int getUniqueDaysCount();

    @Query("SELECT * FROM study_sessions WHERE date >= :startDate ORDER BY date DESC")
    List<StudySessionEntity> getSessionsFromDate(String startDate);
}