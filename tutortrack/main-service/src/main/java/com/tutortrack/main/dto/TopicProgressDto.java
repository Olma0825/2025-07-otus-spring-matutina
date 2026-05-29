package com.tutortrack.main.dto;

import lombok.Data;

import java.util.List;

@Data
public class
TopicProgressDto {
    private Long id;

    private String name;

    private Integer lessonsCount;

    private List<Integer> grades;

    private Double average;
}
