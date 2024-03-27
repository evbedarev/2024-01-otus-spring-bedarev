package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозитьорий jpa для работы  с коментариями")
@DataJpaTest
public class JpaCommentsRepositoryTest {

    private static final long FIRST_BOOK_ID = 1L;

    private static final long FIRST_COMMENT_ID = 1L;

    private static final int COUNT_COMMENTS_OF_FIRST_BOOK = 2;

    private static final String FIRST_COMM_FIRST_BOOK = "Comment_1";

    private static final String SECOND_COMM_FIRST_BOOK = "Comment_11";

    private static final String NEW_COMMENT_STRING = "new_comment";

    private static final long NEW_COMMENT_ID = 5L;

    @Autowired
    CommentRepository jpaCommentRepository;

    @DisplayName("Должен загружать список всех коментариев к книге")
    @Test
    void shouldReturnCorrectCommentListByBookId() {
        List<Comment> comments = jpaCommentRepository.getAllCommentsByBookId(FIRST_BOOK_ID);
        assertThat(comments).isNotNull().hasSize(COUNT_COMMENTS_OF_FIRST_BOOK)
                .allMatch(s -> !s.getText().equals(""))
                .anyMatch(s -> s.getText().equals(FIRST_COMM_FIRST_BOOK))
                .anyMatch(s -> s.getText().equals(SECOND_COMM_FIRST_BOOK));
    }

    @DisplayName("Должен загружать коментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        Optional<Comment> optionalComment = jpaCommentRepository.findById(FIRST_COMMENT_ID);
        assertThat(optionalComment).isPresent().get()
                .matches(s -> s.getText().equals(FIRST_COMM_FIRST_BOOK) && s.getBookId() == FIRST_BOOK_ID);
    }

    @DisplayName("Должен обновлять коментарий по id")
    @Test
    void shouldUpdateCommentById() {
        Comment commentForUpdate = new Comment(FIRST_COMMENT_ID,SECOND_COMM_FIRST_BOOK,FIRST_BOOK_ID);
        jpaCommentRepository.save(commentForUpdate);
        Optional<Comment> commentAfterUpdate = jpaCommentRepository.findById(FIRST_COMMENT_ID);
        assertThat(commentAfterUpdate).isPresent().get()
                .matches(s -> s.getId() == FIRST_COMMENT_ID &&
                        s.getText().equals(SECOND_COMM_FIRST_BOOK) &&
                        s.getBookId() == FIRST_BOOK_ID);

    }

    @DisplayName("Должен вставлять новый коментарий")
    @Test
    void shouldInsertNewComment() {
        Comment commentForInsert = new Comment(0,NEW_COMMENT_STRING,FIRST_BOOK_ID);
        jpaCommentRepository.save(commentForInsert);
        Optional<Comment> newComment = jpaCommentRepository.findById(NEW_COMMENT_ID);
        assertThat(newComment).isPresent().get()
                .matches(s -> s.getBookId() == FIRST_BOOK_ID &&
                        s.getText().equals(NEW_COMMENT_STRING));
    }

    @DisplayName("Должен удалять коментарий по id")
    @Test
    void shouldDeleteCommentById(){
        jpaCommentRepository.deleteById(FIRST_COMMENT_ID);
        Optional<Comment> comment = jpaCommentRepository.findById(FIRST_COMMENT_ID);
        assertThat(comment).isNotPresent();
    }
}
