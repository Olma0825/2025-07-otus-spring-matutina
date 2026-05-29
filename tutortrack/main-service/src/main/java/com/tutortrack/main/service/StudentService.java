package com.tutortrack.main.service;

import com.tutortrack.main.dto.StudentDto;
import com.tutortrack.main.dto.StudentProgressDto;
import com.tutortrack.main.entity.Student;

import java.util.List;

public interface StudentService {

    List<StudentDto> findAll();

    StudentDto findById(Long id);

    StudentDto create(StudentDto dto);

    StudentDto update(Long id, StudentDto dto);

    void deleteById(Long id);

    Student findByUserId(Long userId);

    StudentProgressDto getStudentProgress(Long studentId);

    void linkToUser(Long studentId, Long userId);

    void unlinkFromUser(Long studentId);

}
