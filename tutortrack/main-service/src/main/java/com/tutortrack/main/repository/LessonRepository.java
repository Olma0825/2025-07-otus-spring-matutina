package com.tutortrack.main.repository;

import com.tutortrack.main.entity.Lesson;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByStudentId(Long studentId);

    @EntityGraph(attributePaths = {"lessonTopics"})
    Optional<Lesson> findById(Long id);

}
