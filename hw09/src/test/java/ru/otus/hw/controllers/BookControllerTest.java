package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.dto.BookDetailsDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@DisplayName("Тестирование BookController")
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    private List<BookDto> testBooks;

    private List<AuthorDto> testAuthors;

    private List<GenreDto> testGenres;

    private final long bookId = 100L;

    @BeforeEach
    public void setUp() {
        testAuthors = List.of(
                new AuthorDto(1L, "Author_1"),
                new AuthorDto(2L, "Author_2"),
                new AuthorDto(3L, "Author_3")
        );
        testGenres = List.of(
                new GenreDto(1L, "Genre_1"),
                new GenreDto(2L, "Genre_2"),
                new GenreDto(3L, "Genre_3")
        );
        testBooks = List.of(
                new BookDto(100L, "BookTitle_1", testAuthors.get(0), testGenres.get(0)),
                new BookDto(101L, "BookTitle_2", testAuthors.get(1), testGenres.get(1)),
                new BookDto(102L, "BookTitle_3", testAuthors.get(2), testGenres.get(2))
        );
    }

    @Test
    @DisplayName("Должен возвращать страницу со списком всех книг")
    public void shouldReturnBookPage() throws Exception {
        when(bookService.findAll()).thenReturn(testBooks);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("list"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", hasSize(3)));

        verify(bookService, times(1)).findAll();
    }

    @Test
    @DisplayName("Должен возвращать страницу с одной книгой по ее id")
    public void shouldReturnBookPageById() throws Exception {
        BookDetailsDto bookDetails = new BookDetailsDto(
                testBooks.get(0).id(),
                testBooks.get(0).title(),
                testBooks.get(0).author(),
                testBooks.get(0).genre(),
                List.of()
        );

        when(bookService.findBookByIdWithComments(bookId)).thenReturn(bookDetails);

        mockMvc.perform(get("/book/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("book-details"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", bookDetails));

        verify(bookService, times(1)).findBookByIdWithComments(bookId);
    }

    @Test
    @DisplayName("должен показать форму создания новой книги")
    void shouldShowCreateBookForm() throws Exception {
        when(authorService.findAll()).thenReturn(testAuthors);
        when(genreService.findAll()).thenReturn(testGenres);

        mockMvc.perform(get("/book/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-form"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attributeExists("isEdit"))
                .andExpect(model().attribute("book", instanceOf(BookFormDto.class)))
                .andExpect(model().attribute("authors", testAuthors))
                .andExpect(model().attribute("genres", testGenres))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    @DisplayName("должен обновить существующую книгу")
    void shouldUpdateExistingBook() throws Exception {
        BookDto expectedBook = new BookDto(100L, "Обновлённое название", testAuthors.get(1), testGenres.get(2));

        when(bookService.save(any(BookFormDto.class))).thenReturn(expectedBook);

        mockMvc.perform(post("/book/save")
                        .param("id", "100")
                        .param("title", "Обновлённое название")
                        .param("authorId", "2")
                        .param("genreId", "3")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?success"));

        verify(bookService, times(1)).save(any(BookFormDto.class));
    }

    @Test
    @DisplayName("должен показать форму редактирования книги")
    void shouldShowEditForm() throws Exception {

        when(bookService.findById(bookId)).thenReturn(testBooks.get(0));
        when(authorService.findAll()).thenReturn(testAuthors);
        when(genreService.findAll()).thenReturn(testGenres);

        mockMvc.perform(get("/book/edit/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("book-form"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attributeExists("isEdit"))
                .andExpect(model().attribute("isEdit", true))
                .andExpect(model().attribute("authors", testAuthors))
                .andExpect(model().attribute("genres", testGenres));

        verify(bookService, times(1)).findById(bookId);
        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
    }


    @Test
    @DisplayName("должен удалить книгу и перенаправить на главную")
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).deleteById(bookId);

        mockMvc.perform(delete("/book/delete/{id}", bookId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?success=deleted"));

        verify(bookService, times(1)).deleteById(bookId);
    }

}
