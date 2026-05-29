package com.tutortrack.main.mapper;

import com.tutortrack.main.dto.SubjectDto;
import com.tutortrack.main.entity.Subject;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {
    public SubjectDto toDto(Subject subject) {
        if (subject == null) {
            return null;
        }

        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(subject.getId());
        subjectDto.setName(subject.getName());

        return subjectDto;
    }

    public Subject toEntity(SubjectDto subjectDto) {
        if (subjectDto == null) {
            return null;
        }

        Subject subject = new Subject();
        subject.setId(subjectDto.getId());
        subject.setName(subjectDto.getName());

        return subject;
    }
}
