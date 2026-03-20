package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends MongoRepository<Book, String>, BookRepositoryCustom {

    @Override
    Optional<Book> findById(String id);

    @Override
    List<Book> findAll();

}
