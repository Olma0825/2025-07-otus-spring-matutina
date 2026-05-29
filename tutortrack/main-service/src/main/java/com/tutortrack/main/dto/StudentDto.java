package com.tutortrack.main.dto;

import lombok.Data;

@Data
public class StudentDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String school;

    private Long subjectId;

    private String subjectName;

    private Long userId;

}
