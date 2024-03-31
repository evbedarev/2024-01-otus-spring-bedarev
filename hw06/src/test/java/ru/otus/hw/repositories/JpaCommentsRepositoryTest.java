package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозитьорий jpa для работы  с коментариями")
@DataJpaTest
@Import({JpaCommentRepository.class})
public class JpaCommentsRepositoryTest {

    private static final int FIRST_BOOK_ID = 1;

    private static final int FIRST_COMMENT_ID = 1;

    private static final int COUNT_COMMENTS_OF_FIRST_BOOK = 2;

    private static final String FIRST_COMM_FIRST_BOOK = "Comment_1";

    private static final String SECOND_COMM_FIRST_BOOK = "Comment_11";

    private static final String NEW_COMMENT_STRING = "new_comment";

    private static final int NEW_COMMENT_ID = 5;

    @Autowired
    private JpaCommentRepository jpaCommentRepository;

    @Autowired
    private EntityManager em;

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
                .matches(s -> s.getText().equals(FIRST_COMM_FIRST_BOOK) && s.getBook().getId() == FIRST_BOOK_ID);
    }

    @DisplayName("Должен обновлять коментарий по id")
    @Test
    void shouldUpdateCommentById() {
        Comment commentForUpdate = new Comment(FIRST_COMMENT_ID, SECOND_COMM_FIRST_BOOK, getFirstBook());
        jpaCommentRepository.save(commentForUpdate);
        Comment commentAfterUpdate = em.find(Comment.class, FIRST_COMMENT_ID);
        assertThat(commentAfterUpdate)
                .matches(s -> s.getId() == FIRST_COMMENT_ID &&
                        s.getText().equals(SECOND_COMM_FIRST_BOOK) &&
                        s.getBook().getId() == FIRST_BOOK_ID);

    }

    @DisplayName("Должен вставлять новый коментарий")
    @Test
    void shouldInsertNewComment() {
        Comment commentForInsert = new Comment(0, NEW_COMMENT_STRING, getFirstBook());
        jpaCommentRepository.save(commentForInsert);
        Comment newComment = em.find(Comment.class, NEW_COMMENT_ID);
        assertThat(newComment)
                .matches(s -> s.getBook().getId() == FIRST_BOOK_ID &&
                        s.getText().equals(NEW_COMMENT_STRING));
    }

    @DisplayName("Должен удалять коментарий по id")
    @Test
    void shouldDeleteCommentById() {
        jpaCommentRepository.deleteById(FIRST_COMMENT_ID);
        Optional<Comment> comment = Optional.ofNullable(em.find(Comment.class, FIRST_COMMENT_ID));
        assertThat(comment).isNotPresent();
    }

    Book getFirstBook() {
        return new Book(1L, "title", null, null);
    }
}
