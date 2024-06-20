package ru.otus.hw.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.BookJdbc;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@AllArgsConstructor
public class FinalStepServiceImpl implements FinalStepService {

    private final Logger logger = LoggerFactory.getLogger("MongoBatch");

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void fillTargetTablesAndDropTmp() {
        fillAuthorTargetTable();
        fillGenreTargetTable();
        fillBookTargetTable();
        dropTmpTables();
    }

    private void fillAuthorTargetTable() {
        logger.info("Fill target author table");
        jdbcTemplate.execute("INSERT INTO authors (full_name) " +
                "SELECT full_name FROM tmp_author");
    }

    private void fillGenreTargetTable() {
        logger.info("Fill target author table");
        jdbcTemplate.execute("INSERT INTO genres (name) " +
                "SELECT name FROM tmp_genre");
    }

    private  void fillBookTargetTable() {
        jdbcTemplate.update(
                "INSERT INTO books (title, author_id, genre_id)  " +
                        "SElECT tmp.title, tmp_author.id, tmp_genre.id " +
                        "FROM tmp  " +
                        "LEFT JOIN tmp_author ON tmp_author.mongo_id = tmp.author_mongo_id " +
                        "LEFT JOIN tmp_genre ON tmp_genre.mongo_id = tmp.genre_mongo_id"
        );
    }


    public static RowMapper<BookJdbc> getBookJdbcRowMapper() {
        return new RowMapper<BookJdbc>() {
            @Override
            public BookJdbc mapRow(ResultSet rs, int rowNum) throws SQLException {

                return new BookJdbc(rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4));
            }
        };
    }

    private void dropTmpTables() {
        logger.info("End job");
        logger.info("====Remove temp tables====");
        dropTableIfExists("tmp_author");
        dropTableIfExists("tmp_genre");
        dropTableIfExists("tmp");
    }

    public static RowMapper<Author> getAuthorRowMapper() {
        return new RowMapper<Author>() {
            @Override
            public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Author(rs.getString(1), rs.getString(2));
            }
        };
    }

    public static RowMapper<Genre> getGenreRowMapper() {
        return new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Genre(rs.getString(1), rs.getString(2));
            }
        };
    }

    private void dropTableIfExists(String tableName) {
        try {
            jdbcTemplate.execute("select * from %s".formatted(tableName));
        } catch (BadSqlGrammarException ex) {
            if (ex.getSQLException().getMessage().contains("not found")) {
                logger.info("TABLE %s NOT FOUND".formatted(tableName));
                return;
            }
        }
        jdbcTemplate.execute("DROP TABLE %s".formatted(tableName));
    }
}
