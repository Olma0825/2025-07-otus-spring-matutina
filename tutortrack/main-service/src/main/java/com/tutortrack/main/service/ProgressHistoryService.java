package com.tutortrack.main.service;

import com.tutortrack.main.dto.StudentProgressDto;
import com.tutortrack.main.entity.Lesson;

public interface ProgressHistoryService {

    void createStudentProgress(Long studentId);

    void addLessonToProgress(Long studentId, Lesson lesson);

    void syncLessonInProgress(Long studentId, Long lessonId, Lesson updatedLesson);

    void deleteLessonFromProgress(Long lessonId);

    void deleteStudentProgress(Long studentId);

    StudentProgressDto getStudentProgress(Long studentId);

    StudentProgressDto getMyProgress(Long userId);
}
