package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.BookDetailsDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/")
    public String getBooks(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "list";
    }

    @GetMapping("/book/{id}")
    public String getBookById(@PathVariable("id") long id, Model model) {
        BookDetailsDto book = bookService.findBookByIdWithComments(id);
        model.addAttribute("book", book);
        return "book-details";
    }

    @GetMapping("/book/create")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new BookFormDto());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("isEdit", false);

        return "book-form";
    }

    @PostMapping("/book/save")
    public String createBook(@ModelAttribute BookFormDto book) {
        if (book.getId() == 0) {
            bookService.insert(book.getTitle(), book.getAuthorId(), book.getGenreId());
        } else {
            bookService.update(book.getId(), book.getTitle(), book.getAuthorId(), book.getGenreId());
        }
        return "redirect:/?success";
    }

    @GetMapping("/book/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        BookFormDto book = new BookFormDto(bookService.findById(id));

        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("isEdit", true);

        return "book-form";
    }

    @DeleteMapping("/book/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/?success=deleted";
    }

}
