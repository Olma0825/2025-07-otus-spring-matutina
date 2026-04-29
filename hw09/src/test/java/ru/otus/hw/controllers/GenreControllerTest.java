package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
@DisplayName("Тестирование GenreController")
public class GenreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    private List<GenreDto> testGenres;

    @BeforeEach
    void setUp() {
        testGenres = List.of(
                new GenreDto(1L, "Genre_1"),
                new GenreDto(2L, "Genre_2"),
                new GenreDto(3L, "Genre_3")
        );
    }

    @Test
    @DisplayName("Должен вернуть страницу со списком всех жанров")
    void shouldReturnGenresPage() throws Exception {

        when(genreService.findAll()).thenReturn(testGenres);

        mockMvc.perform(get("/genre"))
                .andExpect(status().isOk())
                .andExpect(view().name("list_genres"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attribute("genres", hasSize(3)));

        verify(genreService, times(1)).findAll();
    }

    @Test
    @DisplayName("Должен корректно отображать пустой список жанров")
    void shouldReturnEmptyGenresList() throws Exception {

        when(genreService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/genre"))
                .andExpect(status().isOk())
                .andExpect(view().name("list_genres"))
                .andExpect(model().attribute("genres", hasSize(0)));

        verify(genreService, times(1)).findAll();
    }
}
