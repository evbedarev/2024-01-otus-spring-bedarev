package ru.otus.hw.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.otus.hw.models.BookText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class TextBookDto {

    @Getter
    @Setter
    private List<String> unformatedText;

    @Getter
    @Setter
    private int partNumber;

    @Getter
    @Setter
    private int maxPartNumber;

    @Getter
    @Setter
    private int maxPage;

    @Getter
    @Setter
    private long bookId;

    @Getter
    @Setter
    private List<String> formattedTextSplitOnPages;



    public static TextBookDto toDto(BookText bookText) {
        List<String> splitedPages = Arrays.asList(bookText.getBookText().split("\n"));
        List<String> pages = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < splitedPages.size(); i++) {
            stringBuilder.append(splitedPages.get(i) + "\n");
            if (i % 50 == 0 && i > 0) {
                pages.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            }
        }
        pages.add(stringBuilder.toString());
        TextBookDto textBookDto = new TextBookDto(Arrays.asList("null"),
                bookText.getPartNumber(),
                bookText.getMaxPartNumber(),
                bookText.getMaxPage(),
                bookText.getBook().getId(),
                pages);
        return textBookDto;
    }
}
