package ru.otus.hw.services;

import ru.otus.hw.models.Bookmark;

import java.util.Optional;

public interface BookmarksService {
    Bookmark insertBookmark(long bookId, int curPage, int partNum);

    Optional<Bookmark> findBookmark(long bookId);
}
