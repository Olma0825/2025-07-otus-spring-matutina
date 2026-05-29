package com.tutortrack.main.dto;

import lombok.Data;

@Data
public class LessonTopicDto {
    private Long id;

    private Long lessonId;

    private Long topicId;

    private String topicName;

    private Integer masteryLevel;

    private String teacherNote;
}
