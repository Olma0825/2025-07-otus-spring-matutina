package com.tutortrack.main.mapper;

import com.tutortrack.main.dto.TopicDto;
import com.tutortrack.main.entity.Topic;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper {
    public TopicDto toDto(Topic topic) {
        TopicDto topicDto = new TopicDto();
        topicDto.setId(topic.getId());
        topicDto.setName(topic.getName());
        topicDto.setSubjectId(topic.getSubject().getId());

        return topicDto;
    }

    public Topic toEntity(TopicDto topicDto) {
        Topic topic = new Topic();
        topic.setId(topicDto.getId());
        topic.setName(topicDto.getName());

        return topic;
    }
}
