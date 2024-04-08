package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repository.BookRepository;
import ru.otus.hw.repository.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    public CommentServiceImpl(CommentRepository commentRepository, BookRepository bookRepository) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Comment> findAllCommentsByBookId(String bookId) {
        List<Comment> comments = commentRepository.getAllCommentsByBookId(bookId);
        if (comments.isEmpty()) {
            throw new EntityNotFoundException("Book with id=%s not found".formatted(bookId));
        }
        return comments;
    }

    @Override
    public Optional<Comment> findById(String id) {
        return commentRepository.findById(id);
    }

    @Transactional
    @Override
    public Comment insert(String text, String bookId) {
        return save("", text, bookId);
    }

    @Transactional
    @Override
    public Comment update(String id, String text) {
        Optional<Comment> comment = findById(id);
        return save(id, text, comment.get().getBook().getId());
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Comment with id %s not found".formatted(id)));
        commentRepository.deleteById(id);
    }

    @Transactional
    public Comment save(String id, String text, String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found".formatted(id)));
        if (id.equals("")) {
            return commentRepository.save(new Comment(text, book));
        }
        comment.setText(text);
        comment.setBook(book);
        return commentRepository.save(comment);
    }
}
