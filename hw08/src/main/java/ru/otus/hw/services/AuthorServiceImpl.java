package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import ru.otus.hw.models.Author;
import ru.otus.hw.repository.AuthorRepository;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }
}
