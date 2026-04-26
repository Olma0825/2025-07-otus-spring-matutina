package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookFormDto {

    private long id;

    private String title;

    private long authorId;

    private long genreId;

    public BookFormDto(BookDto bookDto) {
        if (bookDto != null) {
            this.id = bookDto.id();
            this.title = bookDto.title();
            this.authorId = bookDto.author().id();
            this.genreId = bookDto.genre().id();
        }
    }
}
