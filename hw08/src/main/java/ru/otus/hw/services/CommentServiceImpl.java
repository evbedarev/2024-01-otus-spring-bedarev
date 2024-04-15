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
            throw new EntityNotFoundException("Comments for book with id=%s not found".formatted(bookId));
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
        return save(id, text, "");
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        if (commentRepository.existsById(id)) {
            deleteCommentFromBook(id);
            commentRepository.deleteById(id);
        } else  {
            new EntityNotFoundException("Comment with id %s not found".formatted(id));
        }
    }

    private Comment save(String id, String text, String bookId) {
        if (id.equals("")) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
            Comment comment = commentRepository.save(new Comment(text, book));
            bindCommentWithBook(book, comment);
            return comment;
        }
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found".formatted(id)));
        comment.setText(text);
        return commentRepository.save(comment);
    }

    private void bindCommentWithBook(Book book, Comment comment) {
        List<String> bookComments = book.getCommentsIds();
        bookComments.add(comment.getId());
        book.setComments(bookComments);
        bookRepository.save(book);
    }

    private void deleteCommentFromBook(String commentId) {
        Book book = findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("book not found"))
                .getBook();
        List<String> comments = book.getCommentsIds().stream()
                .filter(c -> !c.equals(commentId)).toList();
        book.setComments(comments);
        bookRepository.save(book);
    }
}
