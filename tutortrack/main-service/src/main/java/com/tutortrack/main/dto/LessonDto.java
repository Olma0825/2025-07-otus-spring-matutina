package com.tutortrack.main.dto;

import com.tutortrack.main.enums.Attendance;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LessonDto {
    private Long id;

    private Long studentId;

    private Long subjectId;

    private LocalDateTime lessonDate;

    private Attendance attendance;

    private List<LessonTopicDto> topics;

}
