package com.tutortrack.main.controller;

import com.tutortrack.main.dto.CreateLessonDto;
import com.tutortrack.main.dto.LessonDto;
import com.tutortrack.main.dto.UpdateLessonDto;
import com.tutortrack.main.exception.EntityNotFoundException;
import com.tutortrack.main.service.LessonService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@AllArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/api/lesson")
    public ResponseEntity<List<LessonDto>> getLessons() {
        List<LessonDto> lessonDtos = lessonService.findAll();
        return ResponseEntity.ok(lessonDtos);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/api/lesson/{id}")
    public ResponseEntity<LessonDto> getLessonById(@PathVariable Long id) {
        try {
            LessonDto lessonDto = lessonService.findById(id);
            return ResponseEntity.ok(lessonDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/api/lesson")
    public ResponseEntity<LessonDto> create(@RequestBody CreateLessonDto createLessonDto) throws URISyntaxException {
        LessonDto createdLesson = lessonService.create(createLessonDto);

        if (createdLesson.getId() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.created(new URI("/api/lesson/" + createdLesson.getId())).body(createdLesson);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/api/lesson/{id}")
    public ResponseEntity<LessonDto> update(@PathVariable Long id, @RequestBody UpdateLessonDto updateLessonDto) {
        try {
            LessonDto updatedLesson = lessonService.update(id, updateLessonDto);
            return ResponseEntity.ok(updatedLesson);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/api/lesson/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/api/lesson/student/{studentId}")
    public ResponseEntity<List<LessonDto>> getLessonsByStudent(@PathVariable Long studentId) {
        List<LessonDto> lessons = lessonService.findByStudentId(studentId);
        return ResponseEntity.ok(lessons);
    }
}
