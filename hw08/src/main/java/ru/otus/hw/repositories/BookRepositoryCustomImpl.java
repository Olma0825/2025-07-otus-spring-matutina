package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final MongoOperations mongoOperations;

    @Override
    public void deleteAllByAuthorId(String authorId) {
        ObjectId objectId = new ObjectId(authorId);
        Query query = new Query(Criteria.where("author.$id").is(objectId));
        mongoOperations.remove(query, Book.class);
    }

    @Override
    public void deleteAllByGenreId(String genreId) {
        ObjectId objectId = new ObjectId(genreId);
        Query query = new Query(Criteria.where("genre.$id").is(objectId));
        mongoOperations.remove(query, Book.class);
    }

    @Override
    public List<String> findIdsByAuthorId(String authorId) {
        ObjectId objectId = new ObjectId(authorId);
        Query query = new Query(Criteria.where("author.$id").is(objectId));
        query.fields().include("_id");
        return mongoOperations.find(query, Book.class)
                .stream()
                .map(Book::getId)
                .toList();
    }

    @Override
    public List<String> findIdsByGenreId(String genreId) {
        ObjectId objectId = new ObjectId(genreId);
        Query query = new Query(Criteria.where("genre.$id").is(objectId));
        query.fields().include("_id");
        return mongoOperations.find(query, Book.class)
                .stream()
                .map(Book::getId)
                .toList();
    }
}
