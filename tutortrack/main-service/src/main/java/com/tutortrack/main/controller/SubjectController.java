package com.tutortrack.main.controller;

import com.tutortrack.main.dto.SubjectDto;
import com.tutortrack.main.exception.EntityNotFoundException;
import com.tutortrack.main.service.SubjectService;
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
public class SubjectController {
    private final SubjectService subjectService;

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/api/subject")
    public ResponseEntity<List<SubjectDto>> getSubjects() {
        List<SubjectDto> subjectDtos = subjectService.findAll();
        return ResponseEntity.ok(subjectDtos);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/api/subject/{id}")
    public ResponseEntity<SubjectDto> getSubjectById(@PathVariable Long id) {
        try {
            SubjectDto subjectDto = subjectService.findById(id);
            return ResponseEntity.ok(subjectDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/api/subject")
    public ResponseEntity<SubjectDto> create(@RequestBody SubjectDto subjectDto) throws URISyntaxException {
        SubjectDto createdSubject = subjectService.create(subjectDto);

        if (createdSubject.getId() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.created(new URI("/api/subject/" + createdSubject.getId())).body(createdSubject);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/api/subject/{id}")
    public ResponseEntity<SubjectDto> update(@PathVariable Long id, @RequestBody SubjectDto subjectDto) {
        try {
            SubjectDto updatedSubject = subjectService.update(id, subjectDto);
            return ResponseEntity.ok(updatedSubject);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/api/subject/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
