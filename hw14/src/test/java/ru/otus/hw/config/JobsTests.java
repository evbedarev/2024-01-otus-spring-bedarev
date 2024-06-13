package ru.otus.hw.config;

import org.junit.jupiter.api.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.BookJdbc;
import ru.otus.hw.models.Genre;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static ru.otus.hw.config.AuthorJobConfig.IMPORT_AUTHOR_JOB_NAME;
import static ru.otus.hw.service.AuthorMigrationServiceImpl.getAuthorRowMapper;
import static ru.otus.hw.config.GenreJobConfig.IMPORT_GENRE_JOB_NAME;
import static ru.otus.hw.service.GenreMigrationServiceImpl.getGenreRowMapper;
import static ru.otus.hw.config.BookJobConfig.IMPORT_BOOK_JOB_NAME;
import static ru.otus.hw.service.BookMigrationServiceImpl.getBookJdbcRowMapper;


@SpringBootTest
@SpringBatchTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JobsTests {

    private static final String GET_BOOK_QUERY = "select * from books";

    private static final String GET_AUTHORS_QUERY = "select * from authors";

    private static final String GET_GENRE_QUERY = "select * from genres";

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    @Qualifier("importGenreJob")
    private Job genreJob;

    @Autowired
    @Qualifier("importAuthorJob")
    private Job authorJob;

    @Autowired
    @Qualifier("importBookJob")
    private Job bookJob;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void initJobs() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    @Order(1)
    public void testAuthorMigrationJob() throws Exception {
        jobLauncherTestUtils.setJob(authorJob);
        JobParameters parameters = new JobParametersBuilder()
                .addDate("startedAt", new Date())
                .toJobParameters();
        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull().extracting(Job::getName).isEqualTo(IMPORT_AUTHOR_JOB_NAME);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        List<Author> authors = jdbcTemplate.query(GET_AUTHORS_QUERY, getAuthorRowMapper());
        assertThat(authors).extracting(Author::getId, Author::getFullName)
                .containsExactly(tuple("1", "Author_1"),
                        tuple("2", "Author_2"),
                        tuple("3", "Author_3"));
    }

    @Test
    @Order(2)
    public void testGenreMigrationJob() throws Exception {
        jobLauncherTestUtils.setJob(genreJob);
        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull().extracting(Job::getName).isEqualTo(IMPORT_GENRE_JOB_NAME);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        List<Genre> genres = jdbcTemplate.query(GET_GENRE_QUERY, getGenreRowMapper());
        assertThat(genres).extracting(Genre::getId, Genre::getName)
                .containsExactly(tuple("1", "Genre_1"),
                        tuple("2", "Genre_2"),
                        tuple("3", "Genre_3"));
    }

    @Test
    @Order(3)
    public void testBookMigrationJob() throws Exception {
        jobLauncherTestUtils.setJob(bookJob);
        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull().extracting(Job::getName).isEqualTo(IMPORT_BOOK_JOB_NAME);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        List<BookJdbc> books = jdbcTemplate.query(GET_BOOK_QUERY, getBookJdbcRowMapper());
        assertThat(books).extracting(BookJdbc::getTitle, BookJdbc::getAuthorId, BookJdbc::getGenreId)
                .containsExactly(tuple("Title_1", "1", "1"),
                        tuple("Title_2", "1", "2"),
                        tuple("Title_3", "3","2"));
    }
}
