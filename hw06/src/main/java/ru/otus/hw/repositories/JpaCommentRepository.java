package ru.otus.hw.repositories;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(em.find(Comment.class,id));
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    public List<Comment> getAllCommentsByBookId(long bookId) {
        TypedQuery<Comment> query = em.createQuery("select b from Comment b where b.bookId = :id", Comment.class);
        query.setParameter("id", bookId);
        List<Comment> comments = query.getResultList();
        return comments;
    }

    @Override
    public void deleteById(long id) {
        Query query = em.createQuery("delete from Comment c where c.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }
}
