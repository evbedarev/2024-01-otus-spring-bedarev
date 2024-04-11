package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    public List<Comment> findAllCommentsByBookId(long bookId) {
        List<Comment> comments = commentRepository.getAllCommentsByBookId(bookId);
        if (comments.isEmpty()) {
            throw new EntityNotFoundException("Book with id=%d not found".formatted(bookId));
        }
        return comments;
    }

    @Override
    public Optional<Comment> findById(long id) {
        return commentRepository.findById(id);
    }

    @Transactional
    @Override
    public Comment insert(String text, long bookId) {
        return save(0, text, bookId);
    }

    @Transactional
    @Override
    public Comment update(long id, String text) {
        return save(id, text, 0);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        }
    }

    private Comment save(long id, String text, long bookId) {
        if (id != 0) {
            Comment comment = commentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(id)));
            return commentRepository.save(new Comment(id, text, comment.getBook()));
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        return commentRepository.save(new Comment(id, text, book));
    }
}
