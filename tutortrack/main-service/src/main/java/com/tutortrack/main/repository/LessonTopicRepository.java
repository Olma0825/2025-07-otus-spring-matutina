package com.tutortrack.main.repository;

import com.tutortrack.main.entity.LessonTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonTopicRepository extends JpaRepository<LessonTopic, Long> {
    List<LessonTopic> findByTopicId(Long id);

    @Query("SELECT lt FROM LessonTopic lt " +
            "JOIN lt.lesson l " +
            "WHERE lt.topic.id = :topicId AND l.student.id = :studentId")
    List<LessonTopic> findByTopicIdAndStudentId(@Param("topicId") Long topicId,
                                                @Param("studentId") Long studentId);
}
