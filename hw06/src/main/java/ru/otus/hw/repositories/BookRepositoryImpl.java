package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public class BookRepositoryImpl implements BookRepository {

    @PersistenceContext
    private EntityManager em;

    public BookRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Book> findById(long id) {
        String jpql = "select b from Book b join fetch b.author left join fetch b.genre where b.id = :id ";
        TypedQuery<Book> query = em.createQuery(jpql, Book.class);
        query.setParameter("id", id);
        List<Book> books = query.getResultList();
        return books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));

    }

    @Override
    public List<Book> findAll() {
        String jpql = "select b from Book b join fetch b.author left join fetch b.genre";
        TypedQuery<Book> query = em.createQuery(jpql, Book.class);
        return query.getResultList();
    }

    @Override
    public Optional<Book> findBookWithComments(long id) {
        String jpql = "select distinct b from Book b " +
                "join fetch b.author " +
                "left join fetch b.genre " +
                "left join fetch b.comments " +
                "where b.id = :id";
        TypedQuery<Book> query = em.createQuery(jpql, Book.class);
        query.setParameter("id", id);

        List<Book> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
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
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
        }
    }

    private Book insert(Book book) {
        em.persist(book);
        return book;
    }

    private Book update(Book book) {
        em.merge(book);
        return book;
    }

}
