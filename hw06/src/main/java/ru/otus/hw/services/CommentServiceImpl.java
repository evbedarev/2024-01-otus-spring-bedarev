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
        Optional<Comment> comment = findById(id);
        return save(id, text, comment.get().getBook().getId());
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Comment with id %d not found".formatted(id)));
        commentRepository.deleteById(id);
    }

    @Transactional
    public Comment save(long id, String text, long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        if (id != 0) {
            commentRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(id)));
            return commentRepository.save(new Comment(id, text, book));
        }
        return commentRepository.save(new Comment(id, text, book));
    }
}
