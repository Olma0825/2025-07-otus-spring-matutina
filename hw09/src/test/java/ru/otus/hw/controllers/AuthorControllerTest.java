package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
@DisplayName("Тестирование AuthorController")
public class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    private List<AuthorDto> testAuthors;

    @BeforeEach
    void setUp() {
        testAuthors = List.of(
                new AuthorDto(1L, "Author_1"),
                new AuthorDto(2L, "Author_2"),
                new AuthorDto(3L, "Author_3")
        );
    }

    @Test
    @DisplayName("Должен вернуть страницу со списком всех авторов")
    void shouldReturnAuthorsPage() throws Exception {

        when(authorService.findAll()).thenReturn(testAuthors);

        mockMvc.perform(get("/author"))
                .andExpect(status().isOk())
                .andExpect(view().name("list_authors"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attribute("authors", hasSize(3)));

        verify(authorService, times(1)).findAll();
    }

    @Test
    @DisplayName("Должен корректно отображать пустой список авторов")
    void shouldReturnEmptyAuthorsList() throws Exception {

        when(authorService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/author"))
                .andExpect(status().isOk())
                .andExpect(view().name("list_authors"))
                .andExpect(model().attribute("authors", hasSize(0)));

        verify(authorService, times(1)).findAll();
    }
}
