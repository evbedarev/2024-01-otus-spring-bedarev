package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.RelatedEntityException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private static final Logger logger = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Author findById(long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id=%d not found".formatted(id)));
        return author;
    }

    @Override
    public Author findByFullName(String fullName) {
        Author author = authorRepository.findByFullName(fullName)
                .orElseThrow(() ->
                        new EntityNotFoundException("Author with fullName %s Not Found".formatted(fullName)));
        return author;
    }

    @Override
    public Author insert(String fullName, String aboutAuthor) {
        checkAuthorExistByFullName(fullName);
        logger.info("Insert new author name: %s".formatted(fullName));
        return authorRepository.save(new Author(0, fullName, aboutAuthor));
    }

    @Override
    public void delete(long id) {
        if (bookRepository.findFirstByAuthorId(id).isPresent()) {
            throw new RelatedEntityException("Author with id %s linked with books.".formatted(id) +
                    "remove links, then delete author");
        }
        if (authorRepository.existsById(id)) {
            logger.info("Delete author with id: %d".formatted(id));
            authorRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Author with id %s not found".formatted(id));
        }
    }

    @Override
    public Author update(long id, String fullName, String aboutAuthor) {
        Optional<Author> optionalAuthor = authorRepository.findById(id);
        if (!optionalAuthor.isPresent()) {
            throw new EntityNotFoundException("Author with id %s not found".formatted(id));
        }
        checkAuthorExistByFullName(fullName);
        logger.info("Update author with id: %d".formatted(id));
        return authorRepository.save(new Author(id, fullName, aboutAuthor));
    }

    private boolean checkAuthorExistByFullName(String fullName) {
        Optional<Author> optionalAuthor = authorRepository.findByFullName(fullName);
        if (optionalAuthor.isPresent()) {
            throw new EntityNotFoundException("Author with fullName: %s already exists".formatted(fullName));
        }
        return false;
    }
}
