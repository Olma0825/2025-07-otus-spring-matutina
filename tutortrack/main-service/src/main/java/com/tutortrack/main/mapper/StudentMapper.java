package com.tutortrack.main.mapper;

import com.tutortrack.main.dto.StudentDto;
import com.tutortrack.main.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentDto toDto(Student student) {

        if (student == null) {
            return null;
        }

        StudentDto studentDto = new StudentDto();
        studentDto.setId(student.getId());
        studentDto.setFirstName(student.getFirstName());
        studentDto.setLastName(student.getLastName());
        studentDto.setSchool(student.getSchool());

        if (student.getSubject() != null) {
            studentDto.setSubjectId(student.getSubject().getId());
            studentDto.setSubjectName(student.getSubject().getName());
        }
        studentDto.setUserId(student.getUserId());

        return studentDto;
    }

    public Student toEntity(StudentDto dto) {
        if (dto == null) {
            return null;
        }

        Student student = new Student();
        student.setId(dto.getId());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setSchool(dto.getSchool());
        student.setUserId(dto.getUserId());

        return student;
    }
}
