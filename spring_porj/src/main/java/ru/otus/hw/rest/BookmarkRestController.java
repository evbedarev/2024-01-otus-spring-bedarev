package ru.otus.hw.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.bookmarks.BookmarkDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Bookmark;
import ru.otus.hw.services.BookmarksService;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class BookmarkRestController {

    private final BookmarksService bookmarksService;

    @PostMapping("/api/v1/bookmarks")
    public BookmarkDto insertBookmark(@RequestBody BookmarkDto bookmarkDto) {
        try {
            Bookmark bookmark = bookmarksService.insertBookmark(bookmarkDto.getBookId(),
                    bookmarkDto.getCurPage(),
                    bookmarkDto.getPartNum());
            return BookmarkDto.toDto(bookmark);
        } catch (EntityNotFoundException exception) {
            return new BookmarkDto(exception.getMessage());
        }
    }

    @GetMapping("/api/v1/bookmarks/{bookId}")
    public BookmarkDto getBookmark(@PathVariable("bookId") long bookId) {
        Optional<Bookmark> bookmark = bookmarksService.findBookmark(bookId);
        if (bookmark.isPresent()) {
            return BookmarkDto.toDto(bookmark.get());
        } else {
            return BookmarkDto.toDtoWithErr("Book not found");
        }
    }
}
