package com.tutortrack.main.service;

import com.tutortrack.main.dto.TopicDto;

import java.util.List;

public interface TopicService {
    List<TopicDto> findAll();

    TopicDto findById(Long id);

    TopicDto create(TopicDto dto);

    TopicDto update(Long id, TopicDto dto);

    void deleteById(Long id);

    List<TopicDto> findBySubjectId(Long subjectId);

}
