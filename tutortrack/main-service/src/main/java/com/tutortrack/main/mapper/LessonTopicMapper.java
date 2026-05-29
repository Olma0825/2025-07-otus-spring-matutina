package com.tutortrack.main.mapper;

import com.tutortrack.main.dto.LessonTopicDto;
import com.tutortrack.main.entity.LessonTopic;
import org.springframework.stereotype.Component;

@Component
public class LessonTopicMapper {
    public LessonTopicDto toDto(LessonTopic lessonTopic) {
        if (lessonTopic == null) {
            return null;
        }
        LessonTopicDto lessonTopicDto = new LessonTopicDto();
        lessonTopicDto.setId(lessonTopic.getId());
        lessonTopicDto.setLessonId(lessonTopic.getLesson().getId());
        lessonTopicDto.setTopicId(lessonTopic.getTopic().getId());
        lessonTopicDto.setTopicName(lessonTopic.getTopic().getName());
        lessonTopicDto.setMasteryLevel(lessonTopic.getMasteryLevel());
        lessonTopicDto.setTeacherNote(lessonTopic.getTeacherNote());

        return lessonTopicDto;
    }

    public LessonTopic toEntity(LessonTopicDto lessonTopicDto) {
        if (lessonTopicDto == null) {
            return null;
        }
        LessonTopic lessonTopic = new LessonTopic();
        lessonTopic.setId(lessonTopicDto.getId());
        //lessonTopic.setTopic(lessonTopicDto.getTopicId());
        //lessonTopic.setLesson(lessonTopicDto.getLessonId());
        lessonTopic.setMasteryLevel(lessonTopicDto.getMasteryLevel());
        lessonTopic.setTeacherNote(lessonTopicDto.getTeacherNote());

        return lessonTopic;
    }

}
