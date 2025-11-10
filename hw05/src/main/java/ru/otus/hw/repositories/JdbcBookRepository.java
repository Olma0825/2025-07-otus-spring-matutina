package ru.otus.hw.repositories;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcOperations jdbc;

    public JdbcBookRepository(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Book> findById(long id) {
        return jdbc.query(
                "select b.id, b.title, b.author_id, a.full_name, b.genre_id, g.name " +
                        "from books b " +
                        "left join authors a on b.author_id = a.id " +
                        "left join genres g on b.genre_id = g.id " +
                        "where b.id = :id "
                ,Map.of("id", id), new BookRowMapper()).stream().findFirst();
    }

    @Override
    public List<Book> findAll() {
        return jdbc.query(
                "select b.id, b.title, b.author_id, a.full_name, b.genre_id, g.name " +
                        "from books b " +
                        "left join authors a on b.author_id = a.id " +
                        "left join genres g on b.genre_id = g.id"
                ,new BookRowMapper());
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        jdbc.update("delete from books where id = :id "
                ,parameters
        );
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("title", book.getTitle());
        parameters.addValue("author_id", book.getAuthor().getId());
        parameters.addValue("genre_id", book.getGenre().getId());
        jdbc.update("insert into books (title, author_id, genre_id) values (:title, :author_id, :genre_id)"
                ,parameters
                ,keyHolder
                ,new String[]{"id"}
        );

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        return book;
    }

    private Book update(Book book) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", book.getId());
        parameters.addValue("title", book.getTitle());
        parameters.addValue("author_id", book.getAuthor().getId());
        parameters.addValue("genre_id", book.getGenre().getId());
        int updatedRows = jdbc.update("update books " +
                        "set title = :title, author_id = :author_id, genre_id = :genre_id " +
                        "where id = :id "
                ,parameters
        );
        if (updatedRows == 0) {
            throw new EntityNotFoundException("Book with id " + book.getId() + " not found");
        }
        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();
            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));

            Author author = new Author();
            author.setId(rs.getLong("author_id"));
            author.setFullName(rs.getString("full_name"));
            book.setAuthor(author);

            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("name"));
            book.setGenre(genre);

            return book;
        }
    }
}
