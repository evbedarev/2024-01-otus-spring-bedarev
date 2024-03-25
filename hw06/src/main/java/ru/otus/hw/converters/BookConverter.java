package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    private final CommentsConverter commentsConverter;

    public String bookToString(Book book) {
        return "Id: %d, title: %s, author: {%s}, genres: [%s], comments: [%s]".formatted(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToString(book.getAuthor()),
                genreConverter.genreToString(book.getGenre()),
                commentsConverter.commentsToString(book.getComments()));
    }
}
