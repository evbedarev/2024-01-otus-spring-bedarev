package ru.otus.hw.dto.bookmarks;

import lombok.Data;
import ru.otus.hw.models.Bookmark;

@Data
public class BookmarkDto {

    private long bookId;

    private int partNum;

    private int curPage;

    private boolean hasError = false;

    private String errMessage;

    public BookmarkDto(long bookId, int partNum, int curPage) {
        this.bookId = bookId;
        this.partNum = partNum;
        this.curPage = curPage;
    }

    public BookmarkDto(String errMessage) {
        this.hasError = true;
        this.errMessage = errMessage;
    }

    public static BookmarkDto toDto(Bookmark bookmark) {
        return new BookmarkDto(bookmark.getBookId(),
                bookmark.getPartNum(),
                bookmark.getCurPage());
    }

    public static BookmarkDto toDtoWithErr(String errMessage) {
        return new BookmarkDto(errMessage);
    }

}
