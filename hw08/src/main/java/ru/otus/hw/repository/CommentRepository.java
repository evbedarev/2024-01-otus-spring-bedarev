package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Comment;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> getAllCommentsByBookId(String bookId);

    void deleteAllCommentsByBookId(String bookId);

}
