package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repository.AuthorRepository;
import ru.otus.hw.repository.BookRepository;
import ru.otus.hw.repository.CommentRepository;
import ru.otus.hw.repository.GenreRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    public BookServiceImpl(AuthorRepository authorRepository,
                           GenreRepository genreRepository,
                           BookRepository bookRepository,
                           CommentRepository commentRepository) {
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional
    @Override
    public Book insert(String title, String authorId, String genreId) {
        return save("", title, authorId, genreId);
    }

    @Transactional
    @Override
    public Book update(String id, String title, String authorId, String genreId) {
        return save(id, title, authorId, genreId);
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        if (bookRepository.existsById(id)) {
            removeDepententComments(bookRepository.findById(id).get().getCommentsIds());
            bookRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Book with id %d not found".formatted(id));
        }
    }

    private Book save(String id, String title, String authorId, String genreId) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
        var genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %s not found".formatted(genreId)));
        if (id.equals("")) {
            return bookRepository.save(new Book(title, author, genre));
        }
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
        book.setTitle(title);
        book.setGenre(genre);
        book.setAuthor(author);
        return bookRepository.save(book);
    }

    private void removeDepententComments(List<String> commentsIds) {
            commentsIds.stream().forEach(ids -> commentRepository.deleteById(ids));
    }
}
