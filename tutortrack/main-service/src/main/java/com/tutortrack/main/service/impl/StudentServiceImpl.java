package com.tutortrack.main.service.impl;

import com.tutortrack.main.dto.StudentDto;
import com.tutortrack.main.dto.StudentProgressDto;
import com.tutortrack.main.dto.TopicProgressDto;
import com.tutortrack.main.entity.LessonTopic;
import com.tutortrack.main.entity.Student;
import com.tutortrack.main.entity.Subject;
import com.tutortrack.main.entity.Topic;
import com.tutortrack.main.exception.EntityNotFoundException;
import com.tutortrack.main.mapper.StudentMapper;
import com.tutortrack.main.repository.LessonTopicRepository;
import com.tutortrack.main.repository.StudentRepository;
import com.tutortrack.main.repository.SubjectRepository;
import com.tutortrack.main.repository.TopicRepository;
import com.tutortrack.main.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    private final TopicRepository topicRepository;

    private final SubjectRepository subjectRepository;

    private final LessonTopicRepository lessonTopicRepository;

    private final StudentMapper studentMapper;

    private final ProgressHistoryServiceImpl progressHistoryService;

    @Override
    public List<StudentDto> findAll() {
        List<Student> students = studentRepository.findAll();

        return students.stream().map(studentMapper::toDto).toList();
    }

    @Override
    public StudentDto findById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student with id=%d not found".formatted(id)));

        return studentMapper.toDto(student);
    }

    @Override
    @Transactional
    public StudentDto create(StudentDto dto) {
        Student student = studentMapper.toEntity(dto);
        student.setId(null);

        if (dto.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + dto.getSubjectId()));
            student.setSubject(subject);
        }

        Student savedStudent = studentRepository.save(student);

        progressHistoryService.createStudentProgress(savedStudent.getId());

        return studentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    public StudentDto update(Long id, StudentDto dto) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student with id=%d not found".formatted(id)));

        if (dto.getFirstName() != null) {
            existingStudent.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            existingStudent.setLastName(dto.getLastName());
        }
        if (dto.getSchool() != null) {
            existingStudent.setSchool(dto.getSchool());
        }

        Student savedStudent = studentRepository.save(existingStudent);
        return studentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        studentRepository.deleteById(id);
        progressHistoryService.deleteStudentProgress(id);
    }

    @Override
    public Student findByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found for user: " + userId));
    }

    @Override
    public StudentProgressDto getStudentProgress(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        List<Topic> topics = topicRepository.findBySubjectId(student.getSubject().getId());
        List<TopicProgressDto> topicsProgress = new ArrayList<>();
        double totalAverage = 0;
        int topicsWithGrades = 0;

        for (Topic topic : topics) {
            List<LessonTopic> lessonTopics = lessonTopicRepository
                    .findByTopicIdAndStudentId(topic.getId(), studentId);

            TopicProgressDto dto = new TopicProgressDto();
            dto.setId(topic.getId());
            dto.setName(topic.getName());
            dto.setLessonsCount(lessonTopics.size());

            List<Integer> grades = lessonTopics.stream()
                    .map(LessonTopic::getMasteryLevel)
                    .collect(Collectors.toList());
            dto.setGrades(grades);

            double avg = grades.stream().mapToInt(Integer::intValue).average().orElse(0);
            dto.setAverage(avg);
            topicsProgress.add(dto);
            if (avg > 0) {
                totalAverage += avg;
                topicsWithGrades++;
            }
        }

        StudentProgressDto result = new StudentProgressDto();
        result.setStudentId(student.getId());
        result.setStudentName(student.getFirstName() + " " + student.getLastName());
        result.setSubjectId(student.getSubject().getId());
        result.setSubjectName(student.getSubject().getName());
        result.setTopics(topicsProgress);
        result.setAverageScore(topicsWithGrades > 0 ? totalAverage / topicsWithGrades : 0);

        return result;
    }

    @Override
    @Transactional
    public void linkToUser(Long studentId, Long userId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        if (student.getUserId() != null) {
            throw new IllegalStateException("Student already linked to user: " + student.getUserId());
        }

        student.setUserId(userId);
        studentRepository.save(student);
    }

    @Override
    @Transactional
    public void unlinkFromUser(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        student.setUserId(null);
        studentRepository.save(student);
    }

}
