package ru.otus.hw.repositories;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private EntityManager em;



    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> entityGraphGenres = em.getEntityGraph("genres-author-entity-graph");
        try {
            TypedQuery<Book> query = em.createQuery("select distinct b from Book b where id = :id", Book.class);
            query.setHint("jakarta.persistence.fetchgraph", entityGraphGenres);
            query.setParameter("id", id);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraphGenres = em.getEntityGraph("genres-author-entity-graph");
        TypedQuery<Book> query = em.createQuery("select distinct b from Book b", Book.class);
        query.setHint("jakarta.persistence.fetchgraph", entityGraphGenres);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        return em.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Query query = em.createQuery("delete from Book b where b.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }
}
