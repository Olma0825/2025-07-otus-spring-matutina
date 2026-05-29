package com.tutortrack.main.service.impl;

import com.tutortrack.main.dto.TopicDto;
import com.tutortrack.main.entity.Subject;
import com.tutortrack.main.entity.Topic;
import com.tutortrack.main.exception.EntityNotFoundException;
import com.tutortrack.main.mapper.TopicMapper;
import com.tutortrack.main.repository.SubjectRepository;
import com.tutortrack.main.repository.TopicRepository;
import com.tutortrack.main.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;

    private final SubjectRepository subjectRepository;

    private final TopicMapper topicMapper;

    @Override
    public List<TopicDto> findAll() {
        List<Topic> topics = topicRepository.findAll();

        return topics.stream().map(topicMapper::toDto).toList();
    }

    @Override
    public TopicDto findById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic with id=%d not found".formatted(id)));

        return topicMapper.toDto(topic);
    }

    @Override
    @Transactional
    public TopicDto create(TopicDto dto) {
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException("Subject with id=%d not found"
                        .formatted(dto.getSubjectId())));

        Topic topic = topicMapper.toEntity(dto);
        topic.setId(null);
        topic.setSubject(subject);
        Topic savedTopic = topicRepository.save(topic);
        return topicMapper.toDto(savedTopic);
    }

    @Override
    @Transactional
    public TopicDto update(Long id, TopicDto dto) {
        Topic existingTopic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic with id=%d not found".formatted(id)));

        if (dto.getName() != null) {
            existingTopic.setName(dto.getName());
        }

        Topic savedTopic = topicRepository.save(existingTopic);
        return topicMapper.toDto(savedTopic);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        topicRepository.deleteById(id);
    }

    @Override
    public List<TopicDto> findBySubjectId(Long subjectId) {
        return topicRepository.findBySubjectId(subjectId).stream().map(topicMapper::toDto).toList();
    }
}
