package com.tutortrack.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/students")
    public String studentsPage() {
        return "students";
    }

    @GetMapping("/subjects")
    public String subjectsPage() {
        return "subjects";
    }

    @GetMapping("/ratings")
    public String ratingsPage() {
        return "ratings";
    }

    @GetMapping("/student-details")
    public String studentDetailsPage() {
        return "student-details";
    }

    @GetMapping("/link-students")
    public String linkStudentsPage() {
        return "link-students";
    }
}
