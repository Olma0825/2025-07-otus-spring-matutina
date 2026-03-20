package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
    private final MongoOperations mongoOperations;

    public void deleteAllByBookIds(List<String> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return;
        }
        List<ObjectId> objectIds = bookIds.stream().map(ObjectId::new).collect(Collectors.toList());
        Query query = new Query(Criteria.where("book.$id").in(objectIds));

        mongoOperations.remove(query, Comment.class);

    }
}
