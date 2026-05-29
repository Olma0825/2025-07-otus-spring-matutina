package com.tutortrack.main.repository;

import com.tutortrack.main.document.ProgressHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgressHistoryRepository extends MongoRepository<ProgressHistory, String> {
    Optional<ProgressHistory> findByStudentId(Long studentId);

    @Query("{ 'lessons.lessonId': ?0 }")
    Optional<ProgressHistory> findByLessonId(Long lessonId);

    void deleteByStudentId(Long studentId);

}
