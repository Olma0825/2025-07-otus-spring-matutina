package com.tutortrack.main.mapper;

import com.tutortrack.main.dto.LessonDto;
import com.tutortrack.main.entity.Lesson;
import com.tutortrack.main.entity.LessonTopic;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class LessonMapper {

    private final LessonTopicMapper lessonTopicMapper;

    public LessonDto toDto(Lesson lesson) {

        if (lesson == null) {
            return null;
        }

        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(lesson.getId());
        lessonDto.setStudentId(lesson.getStudent() != null ? lesson.getStudent().getId() : null);
        lessonDto.setSubjectId(lesson.getSubject() != null ? lesson.getSubject().getId() : null);
        lessonDto.setLessonDate(lesson.getLessonDate());
        lessonDto.setAttendance(lesson.getAttendance());

        List<LessonTopic> topics = lesson.getLessonTopics();
        lessonDto.setTopics(topics.stream().map(lessonTopicMapper::toDto).toList());

        return lessonDto;
    }

    public Lesson toEntity(LessonDto dto) {
        if (dto == null) {
            return null;
        }

        Lesson lesson = new Lesson();
        lesson.setId(dto.getId());
        lesson.setLessonDate(dto.getLessonDate());
        lesson.setAttendance(dto.getAttendance());

        return lesson;
    }
}
