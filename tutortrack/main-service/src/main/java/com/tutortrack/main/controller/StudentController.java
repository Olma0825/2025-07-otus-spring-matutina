package com.tutortrack.main.controller;

import com.tutortrack.main.dto.StudentDto;
import com.tutortrack.main.dto.StudentProgressDto;
import com.tutortrack.main.entity.Student;
import com.tutortrack.main.exception.EntityNotFoundException;
import com.tutortrack.main.service.StudentService;
import com.tutortrack.main.service.impl.ProgressHistoryServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import java.util.Map;

@RestController
@AllArgsConstructor
public class StudentController {

    private final StudentService studentService;

    private final ProgressHistoryServiceImpl progressHistoryService;

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/api/student")
    public ResponseEntity<List<StudentDto>> getStudents() {
        List<StudentDto> studentDtos = studentService.findAll();
        return ResponseEntity.ok(studentDtos);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/api/student/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        try {
            StudentDto studentDto = studentService.findById(id);
            return ResponseEntity.ok(studentDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/api/student")
    public ResponseEntity<StudentDto> create(@RequestBody StudentDto studentDto) throws URISyntaxException {
        StudentDto createdStudent = studentService.create(studentDto);

        if (createdStudent.getId() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.created(new URI("/api/student/" + createdStudent.getId())).body(createdStudent);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/api/student/{id}")
    public ResponseEntity<StudentDto> update(@PathVariable Long id, @RequestBody StudentDto studentDto) {
        try {
            StudentDto updatedStudent = studentService.update(id, studentDto);
            return ResponseEntity.ok(updatedStudent);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/api/student/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/api/student/{studentId}/progress")
    public ResponseEntity<StudentProgressDto> getStudentProgress(@PathVariable Long studentId) {
        //StudentProgressDto progress = studentService.getStudentProgress(studentId);
        try {
            StudentProgressDto progress = progressHistoryService.getStudentProgress(studentId);
            return ResponseEntity.ok(progress);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/student/my/progress")
    public ResponseEntity<?> getMyProgress(@AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        Student student = studentService.findByUserId(userId);
        if (student == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Student not linked to user",
                            "message", "Ваш профиль не связан с учеником. Обратитесь к учителю."));
        }
        //StudentProgressDto progress = studentService.getStudentProgress(student.getId());
        StudentProgressDto progress = progressHistoryService.getMyProgress(userId);
        return ResponseEntity.ok(progress);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/api/student/{id}/link")
    public ResponseEntity<Void> linkStudentToUser(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        studentService.linkToUser(id, userId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/api/student/{id}/unlink")
    public ResponseEntity<Void> unlinkStudent(@PathVariable Long id) {
        studentService.unlinkFromUser(id);
        return ResponseEntity.ok().build();
    }

}
