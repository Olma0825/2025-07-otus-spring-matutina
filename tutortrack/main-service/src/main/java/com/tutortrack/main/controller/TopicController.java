package com.tutortrack.main.controller;

import com.tutortrack.main.dto.TopicDto;
import com.tutortrack.main.exception.EntityNotFoundException;
import com.tutortrack.main.service.TopicService;
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
public class TopicController {
    private final TopicService topicService;

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/api/topic")
    public ResponseEntity<List<TopicDto>> getTopics() {
        List<TopicDto> topicDtos = topicService.findAll();
        return ResponseEntity.ok(topicDtos);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/api/topic/{id}")
    public ResponseEntity<TopicDto> getTopicById(@PathVariable Long id) {
        try {
            TopicDto topicDto = topicService.findById(id);
            return ResponseEntity.ok(topicDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/api/topic")
    public ResponseEntity<TopicDto> create(@RequestBody TopicDto topicDto) throws URISyntaxException {
        TopicDto createdTopic = topicService.create(topicDto);

        if (createdTopic.getId() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.created(new URI("/api/topic/" + createdTopic.getId())).body(createdTopic);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/api/topic/{id}")
    public ResponseEntity<TopicDto> update(@PathVariable Long id, @RequestBody TopicDto topicDto) {
        try {
            TopicDto updatedTopic = topicService.update(id, topicDto);
            return ResponseEntity.ok(updatedTopic);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/api/topic/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        topicService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/api/topic/subject/{subjectId}")
    public ResponseEntity<List<TopicDto>> getTopicsBySubjectId(@PathVariable Long subjectId) {
        List<TopicDto> topicDtos = topicService.findBySubjectId(subjectId);

        return ResponseEntity.ok(topicDtos);
    }
}
