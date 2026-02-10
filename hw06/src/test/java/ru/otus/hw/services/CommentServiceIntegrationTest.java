package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.otus.hw.dto.CommentDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Интеграционные тесты CommentService")
public class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Test
    @Sql(scripts = "/data.sql")
    @DisplayName("Должен загружать комментарий без LazyInitializationException")
    void shouldReturnBookById() {
        CommentDto commentDto = commentService.findById(1000L);

        assertThat(commentDto.id()).isEqualTo(1000L);
        assertThat(commentDto.body()).isEqualTo("comment_1_1");
        assertThat(commentDto.createdAt()).isNotNull();
    }

}
