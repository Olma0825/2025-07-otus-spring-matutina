package com.tutortrack.main.service.impl;

import com.tutortrack.main.document.ProgressHistory;
import com.tutortrack.main.dto.StudentProgressDto;
import com.tutortrack.main.dto.TopicProgressDto;
import com.tutortrack.main.entity.Lesson;
import com.tutortrack.main.entity.Student;
import com.tutortrack.main.exception.EntityNotFoundException;
import com.tutortrack.main.repository.LessonTopicRepository;
import com.tutortrack.main.repository.ProgressHistoryRepository;
import com.tutortrack.main.repository.StudentRepository;
import com.tutortrack.main.repository.TopicRepository;
import com.tutortrack.main.service.ProgressHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ProgressHistoryServiceImpl implements ProgressHistoryService {

    private final ProgressHistoryRepository progressHistoryRepository;

    private final StudentRepository studentRepository;

    private final TopicRepository topicRepository;

    private final LessonTopicRepository lessonTopicRepository;

    @Override
    @Transactional
    public void createStudentProgress(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + studentId));

        ProgressHistory history = new ProgressHistory();
        history.setStudentId(studentId);
        history.setStudentName(student.getFirstName() + " " + student.getLastName());
        history.setUserId(student.getUserId());
        history.setSubjectId(student.getSubject().getId());
        history.setSubjectName(student.getSubject().getName());

        progressHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void addLessonToProgress(Long studentId, Lesson lesson) {
        ProgressHistory history = progressHistoryRepository
                .findByStudentId(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Progress not found for student: " + studentId));

        boolean lessonExists = history.getLessons().stream()
                .anyMatch(l -> l.getLessonId().equals(lesson.getId()));

        if (lessonExists) {
            return;  // такого урока не должно быть по идее
        }

        ProgressHistory.LessonDoc lessonDoc = new ProgressHistory.LessonDoc();
        lessonDoc.setLessonId(lesson.getId());
        lessonDoc.setLessonDate(lesson.getLessonDate());
        lessonDoc.setAttendance(lesson.getAttendance());

        List<ProgressHistory.LessonDoc.TopicDoc> topicDocs = lesson.getLessonTopics()
                .stream()
                .map(t -> {
                    ProgressHistory.LessonDoc.TopicDoc topicDoc = new ProgressHistory.LessonDoc.TopicDoc();
                    topicDoc.setTopicId(t.getTopic().getId());
                    topicDoc.setTopicName(t.getTopic().getName());
                    topicDoc.setMasteryLevel(t.getMasteryLevel());
                    topicDoc.setTeacherNote(t.getTeacherNote());
                    return topicDoc;
                }).toList();
        lessonDoc.setTopicDocs(topicDocs);

        List<ProgressHistory.LessonDoc> lessonDocs = history.getLessons();
        lessonDocs.add(lessonDoc);
        history.setLessons(lessonDocs);

        progressHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void syncLessonInProgress(Long studentId, Long lessonId, Lesson updatedLesson) {

        ProgressHistory history = progressHistoryRepository
                .findByStudentId(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Progress not found for student: " + studentId));

        ProgressHistory.LessonDoc lessonDoc = history.getLessons().stream()
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found in progress: " + lessonId));

        lessonDoc.setLessonDate(updatedLesson.getLessonDate());
        lessonDoc.setAttendance(updatedLesson.getAttendance());

        List<ProgressHistory.LessonDoc.TopicDoc> newTopics = updatedLesson.getLessonTopics().stream()
                .map(lt -> {
                    ProgressHistory.LessonDoc.TopicDoc topicDoc = new ProgressHistory.LessonDoc.TopicDoc();
                    topicDoc.setTopicId(lt.getTopic().getId());
                    topicDoc.setTopicName(lt.getTopic().getName());
                    topicDoc.setMasteryLevel(lt.getMasteryLevel());
                    topicDoc.setTeacherNote(lt.getTeacherNote());
                    return topicDoc;
                })
                .toList();

        lessonDoc.getTopicDocs().clear();
        lessonDoc.getTopicDocs().addAll(newTopics);

        progressHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void deleteLessonFromProgress(Long lessonId) {
        ProgressHistory progressHistory = progressHistoryRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("ProgressHistory not found for lesson: " + lessonId));

        progressHistory.getLessons().removeIf(l -> l.getLessonId().equals(lessonId));

        progressHistoryRepository.save(progressHistory);
    }

    @Override
    @Transactional
    public void deleteStudentProgress(Long studentId) {
        progressHistoryRepository.deleteByStudentId(studentId);

    }

    @Override
    public StudentProgressDto getStudentProgress(Long studentId) {
        ProgressHistory history = progressHistoryRepository
                .findByStudentId(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Progress not found for student: " + studentId));

        Map<Long, List<Integer>> gradesByTopic = new HashMap<>();
        Map<Long, String> topicNames = new HashMap<>();

        for (ProgressHistory.LessonDoc lesson : history.getLessons()) {
            for (ProgressHistory.LessonDoc.TopicDoc topic : lesson.getTopicDocs()) {
                gradesByTopic.computeIfAbsent(topic.getTopicId(), k -> new ArrayList<>())
                        .add(topic.getMasteryLevel());
                topicNames.put(topic.getTopicId(), topic.getTopicName());
            }
        }

        List<TopicProgressDto> topicsProgress = new ArrayList<>();
        double totalAverage = 0;
        int topicsWithGrades = 0;

        for (Map.Entry<Long, List<Integer>> entry : gradesByTopic.entrySet()) {
            Long topicId = entry.getKey();
            List<Integer> grades = entry.getValue();

            double avg = grades.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0);

            TopicProgressDto dto = new TopicProgressDto();
            dto.setId(topicId);
            dto.setName(topicNames.get(topicId));
            dto.setLessonsCount(grades.size());
            dto.setGrades(grades);
            dto.setAverage(avg);
            topicsProgress.add(dto);

            if (avg > 0) {
                totalAverage += avg;
                topicsWithGrades++;
            }
        }

        double averageScore = topicsWithGrades > 0 ? totalAverage / topicsWithGrades : 0;

        StudentProgressDto result = new StudentProgressDto();
        result.setStudentId(history.getStudentId());
        result.setStudentName(history.getStudentName());
        result.setSubjectId(history.getSubjectId());
        result.setSubjectName(history.getSubjectName());
        result.setAverageScore(averageScore);
        result.setTopics(topicsProgress);

        return result;
    }

    @Override
    public StudentProgressDto getMyProgress(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found for user: " + userId));

        return getStudentProgress(student.getId());
    }

}
