package ru.otus.hw.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Bookmark;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.BookmarksRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BookmarkServiceImpl implements BookmarksService {

    private static final Logger logger = LoggerFactory.getLogger(BookmarkServiceImpl.class);

    private final BookmarksRepository bookmarksRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Bookmark insertBookmark(long bookId, int curPage, int partNum) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (bookmarksRepository.findByBookIdAndUsername(bookId, username).isPresent()) {
           bookmarksRepository.deleteByBookIdAndUsername(bookId, username);
           logger.info("Delete old bookmark for bookId: %d".formatted(bookId));
        }
        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException("Book with id %s not found".formatted(bookId));
        }
        Bookmark bookmark = new Bookmark(0, username, bookId, partNum, curPage);
        logger.info("Create bookmark for bookId: %d, current page: %d, part number: %d"
                .formatted(bookId, curPage, partNum));
        return bookmarksRepository.save(bookmark);
    }

    @Override
    public Optional<Bookmark> findBookmark(long bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return bookmarksRepository.findByBookIdAndUsername(bookId, authentication.getName());
    }
}
