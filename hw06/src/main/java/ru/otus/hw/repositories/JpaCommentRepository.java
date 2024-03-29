package ru.otus.hw.repositories;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private final EntityManager em;

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
        TypedQuery<Comment> query = em.createQuery("select b from Comment b where b.book.id = :id", Comment.class);
        query.setParameter("id", bookId);
        List<Comment> comments = query.getResultList();
        return comments;
    }

    @Override
    public void deleteById(long id) {
        em.remove(findById(id).get());
    }
}
