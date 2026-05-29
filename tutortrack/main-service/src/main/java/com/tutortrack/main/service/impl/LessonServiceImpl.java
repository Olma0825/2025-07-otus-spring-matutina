package com.tutortrack.main.service.impl;

import com.tutortrack.main.dto.CreateLessonDto;
import com.tutortrack.main.dto.LessonDto;
import com.tutortrack.main.dto.LessonTopicDto;
import com.tutortrack.main.dto.UpdateLessonDto;
import com.tutortrack.main.entity.Lesson;
import com.tutortrack.main.entity.LessonTopic;
import com.tutortrack.main.entity.Student;
import com.tutortrack.main.entity.Subject;
import com.tutortrack.main.entity.Topic;
import com.tutortrack.main.exception.EntityNotFoundException;
import com.tutortrack.main.mapper.LessonMapper;
import com.tutortrack.main.repository.LessonRepository;
import com.tutortrack.main.repository.StudentRepository;
import com.tutortrack.main.repository.SubjectRepository;
import com.tutortrack.main.repository.TopicRepository;
import com.tutortrack.main.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;

    private final StudentRepository studentRepository;

    private final SubjectRepository subjectRepository;

    private final TopicRepository topicRepository;

    private final LessonMapper lessonMapper;

    private final ProgressHistoryServiceImpl progressHistoryService;

    @Override
    public List<LessonDto> findAll() {
        List<Lesson> lessons = lessonRepository.findAll();

        return lessons.stream().map(lessonMapper::toDto).toList();
    }

    @Override
    public LessonDto findById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson with id=%d not found".formatted(id)));

        return lessonMapper.toDto(lesson);
    }

    @Override
    @Transactional
    public LessonDto create(CreateLessonDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student with id=%d not found"
                        .formatted(dto.getStudentId())));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException("Subject with id=%d not found"
                        .formatted(dto.getSubjectId())));

        Lesson lesson = new Lesson();
        lesson.setId(null);
        lesson.setLessonDate(dto.getLessonDate());
        lesson.setAttendance(dto.getAttendance());
        lesson.setStudent(student);
        lesson.setSubject(subject);

        if (dto.getTopics() != null) {
            for (LessonTopicDto topicDto : dto.getTopics()) {
                Topic topic = topicRepository.findById(topicDto.getTopicId())
                        .orElseThrow(() -> new EntityNotFoundException("Topic with id=%d not found"
                                .formatted(topicDto.getTopicId())));

                LessonTopic lessonTopic = new LessonTopic();
                lessonTopic.setLesson(lesson);
                lessonTopic.setTopic(topic);
                lessonTopic.setMasteryLevel(topicDto.getMasteryLevel());
                lessonTopic.setTeacherNote(topicDto.getTeacherNote());

                lesson.getLessonTopics().add(lessonTopic);
            }
        }

        Lesson savedLesson = lessonRepository.save(lesson);

        progressHistoryService.addLessonToProgress(student.getId(), savedLesson);

        return lessonMapper.toDto(savedLesson);
    }

    @Override
    @Transactional
    public LessonDto update(Long id, UpdateLessonDto dto) {
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found"));

        // Обновляем простые поля
        if (dto.getLessonDate() != null) {
            existingLesson.setLessonDate(dto.getLessonDate());
        }
        if (dto.getAttendance() != null) {
            existingLesson.setAttendance(dto.getAttendance());
        }

        existingLesson.getLessonTopics().clear();

        lessonRepository.flush();

        if (dto.getTopics() != null && !dto.getTopics().isEmpty()) {
            for (LessonTopicDto topicDto : dto.getTopics()) {
                if (topicDto.getTopicId() == null) continue;

                Topic topic = topicRepository.findById(topicDto.getTopicId())
                        .orElseThrow(() -> new EntityNotFoundException("Topic not found"));

                LessonTopic lessonTopic = new LessonTopic();
                lessonTopic.setLesson(existingLesson);
                lessonTopic.setTopic(topic);
                lessonTopic.setMasteryLevel(topicDto.getMasteryLevel() != null ? topicDto.getMasteryLevel() : 3);
                lessonTopic.setTeacherNote(topicDto.getTeacherNote());

                existingLesson.getLessonTopics().add(lessonTopic);
            }
        }

        progressHistoryService.syncLessonInProgress(existingLesson.getStudent().getId(),
                existingLesson.getId(), existingLesson);

        return lessonMapper.toDto(existingLesson);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        lessonRepository.deleteById(id);
        progressHistoryService.deleteLessonFromProgress(id);
    }

    @Override
    public List<LessonDto> findByStudentId(Long studentId) {

        List<Lesson> lessons = lessonRepository.findByStudentId(studentId);

        return lessons.stream().map(lessonMapper::toDto).toList();
    }
}
