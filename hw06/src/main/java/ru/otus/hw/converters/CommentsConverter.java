package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentsConverter {
    public String commentToString(Comment comment) {
        return "Id: %d, Text: %s BookId: %s".formatted(comment.getId(), comment.getText(),
                comment.getBook().getId());
    }

    public String commentsToString(List<Comment> comments) {
        return comments == null ? " " : comments.stream().
                map(c -> "Id: %d, Text: %s".formatted(c.getId(),c.getText())).
                collect(Collectors.joining(","));
    }
}
