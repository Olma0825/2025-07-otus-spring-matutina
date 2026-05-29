package com.tutortrack.main.service.impl;

import com.tutortrack.main.dto.SubjectDto;
import com.tutortrack.main.entity.Subject;
import com.tutortrack.main.exception.EntityNotFoundException;
import com.tutortrack.main.mapper.SubjectMapper;
import com.tutortrack.main.repository.SubjectRepository;
import com.tutortrack.main.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;

    private final SubjectMapper subjectMapper;

    @Override
    public List<SubjectDto> findAll() {
        List<Subject> subjects = subjectRepository.findAll();

        return subjects.stream().map(subjectMapper::toDto).toList();
    }

    @Override
    public SubjectDto findById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject with id=%d not found".formatted(id)));

        return subjectMapper.toDto(subject);
    }

    @Override
    @Transactional
    public SubjectDto create(SubjectDto dto) {
        Subject subject = subjectMapper.toEntity(dto);
        subject.setId(null);
        Subject savedSubject = subjectRepository.save(subject);
        return subjectMapper.toDto(savedSubject);
    }

    @Override
    @Transactional
    public SubjectDto update(Long id, SubjectDto dto) {
        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject with id=%d not found".formatted(id)));

        if (dto.getName() != null) {
            existingSubject.setName(dto.getName());
        }

        Subject savedSubject = subjectRepository.save(existingSubject);
        return subjectMapper.toDto(savedSubject);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        subjectRepository.deleteById(id);
    }
}
