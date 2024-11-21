package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private static final int PAGE_SIZE = 5;

    private static final String SORTED_BY = "id";

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookTextRepository bookTextRepository;

    private final BookmarksRepository bookmarksRepository;


    @Override
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Page<Book> findAllInPages(int pageNum) {
        Pageable firstFiveElm = PageRequest.of(pageNum, PAGE_SIZE, Sort.by(SORTED_BY));
        Page<Book> bookPage = bookRepository.findAll(firstFiveElm);
        return bookPage;
    }

    @Override
    public List<Book> findByAuthorFullName(String fullName) {
        return bookRepository.findByAuthorFullName(fullName);
    }

    @Override
    public Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Transactional
    @Override
    public Book insert(String title, long authorId, long genreId) {
        logger.info("Insert new book title: %s, authorId: %s, genreId: %s".formatted(
                title, authorId, genreId));
        return save(0, title, authorId, genreId);
    }

    @Override
    @Transactional
    public Book update(long id, String title, long authorId, long genreId) {
        logger.info("Update book with id: %s ,title: %s, authorId: %s, genreId: %s".formatted(
                id, title, authorId, genreId));
        return save(id, title, authorId, genreId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(long id) {
        if (bookmarksRepository.existsByBookId(id)) {
            bookmarksRepository.deleteByBookId(id);
            logger.info("delete bookmark by bookId: %s".formatted(id));
        }
        if (bookTextRepository.existsByBookId(id)) {
            bookTextRepository.deleteByBookId(id);
            logger.info("delete book_text by bookId: %s".formatted(id));
        }
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            logger.info("delete book with id: %s".formatted(id));
        } else {
            throw new EntityNotFoundException("Book with id=%d not found".formatted(id));
        }
    }

    private Book save(long id, String title, long authorId, long genreId) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(genreId)));
        var book = new Book(id, title, author, genre);
        return bookRepository.save(book);
    }
}
