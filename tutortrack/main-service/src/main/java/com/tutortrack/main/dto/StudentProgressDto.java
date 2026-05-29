package com.tutortrack.main.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudentProgressDto {
    private Long studentId;

    private String studentName;

    private Long subjectId;

    private String subjectName;

    private Double averageScore;

    private List<TopicProgressDto> topics;
}
