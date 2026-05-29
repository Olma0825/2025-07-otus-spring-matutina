package com.tutortrack.main.service;

import com.tutortrack.main.dto.CreateLessonDto;
import com.tutortrack.main.dto.LessonDto;
import com.tutortrack.main.dto.UpdateLessonDto;

import java.util.List;

public interface LessonService {
    List<LessonDto> findAll();

    LessonDto findById(Long id);

    LessonDto create(CreateLessonDto dto);

    LessonDto update(Long id, UpdateLessonDto dto);

    void deleteById(Long id);

    List<LessonDto> findByStudentId(Long studentId);

}
