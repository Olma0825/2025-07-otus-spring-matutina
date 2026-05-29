package com.tutortrack.main.document;

import com.tutortrack.main.enums.Attendance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "history_progress")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressHistory {
    @Id
    private String id;

    private Long studentId;

    private String studentName;

    private Long userId;

    private Long subjectId;

    private String subjectName;

    private List<LessonDoc> lessons = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonDoc {
        private Long lessonId;

        private LocalDateTime lessonDate;

        private Attendance attendance;

        private List<TopicDoc> topicDocs = new ArrayList<>();

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TopicDoc {
            private Long topicId;

            private String topicName;

            private int masteryLevel;

            private String teacherNote;
        }
    }


}
