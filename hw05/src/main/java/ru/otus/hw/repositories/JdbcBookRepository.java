package ru.otus.hw.repositories;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcOperations jdbc;

    public JdbcBookRepository(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Book> findById(long id) {
        Book book;
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("id", id);
        try {
            book = jdbc.queryForObject("SELECT books.id as id, books.title as title," +
                    " books.author_id as author_id, books.genre_id as genre_id, authors.full_name as author_name," +
                    " genres.name as genre_name FROM books INNER JOIN authors ON" +
                    " books.author_id = authors.id AND books.id = :id" +
                    " INNER JOIN genres ON books.genre_id = genres.id",param, new BookRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
        return Optional.of(book);
    }

    @Override
    public List<Book> findAll() {
        return jdbc.query("SELECT books.id as id, books.title as title," +
                " books.author_id as author_id, books.genre_id as genre_id, authors.full_name as author_name," +
                " genres.name as genre_name FROM books LEFT JOIN authors ON books.author_id = authors.id" +
                " LEFT JOIN genres ON books.genre_id = genres.id", new BookRowMapper());
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        Optional<Book> book = findById(id);
        if (book.isEmpty()) {
            throw new EntityNotFoundException(String.format("Book with id = %s not found", id));
        }
        jdbc.update("delete from books where id = :id",params);
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("genre_id", book.getGenre().getId());
        jdbc.update("INSERT INTO books (title, author_id, genre_id) VALUES (:title, :author_id, :genre_id)",
                params,keyHolder,new String[]{"id"});
        book.setId(keyHolder.getKeyAs(Long.class));
        return book;
    }

    private Book update(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", book.getId());
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("genre_id", book.getGenre().getId());
        Optional<Book> checkBook = findById(book.getId());
        if (checkBook.isEmpty()) {
            throw new EntityNotFoundException(String.format("Book with id = %s not found", book.getId()));
        }
        jdbc.update("update books set title = :title, author_id = :author_id, genre_id = :genre_id where id = :id"
                ,params);
        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("id");
            String title = rs.getString("title");
            Author author = new Author(rs.getLong("author_id"),
                    rs.getString("author_name"));
            Genre genre = new Genre(rs.getLong("genre_id"),
                    rs.getString("genre_name"));
            return new Book(id,title,author,genre);
        }
    }
}
