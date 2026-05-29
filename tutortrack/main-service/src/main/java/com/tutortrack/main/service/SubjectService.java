package com.tutortrack.main.service;

import com.tutortrack.main.dto.SubjectDto;

import java.util.List;

public interface SubjectService {
    List<SubjectDto> findAll();

    SubjectDto findById(Long id);

    SubjectDto create(SubjectDto dto);

    SubjectDto update(Long id, SubjectDto dto);

    void deleteById(Long id);
}
