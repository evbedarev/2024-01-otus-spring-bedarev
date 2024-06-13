package ru.otus.hw.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.BookJdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@Service
public class BookMigrationServiceImpl implements BookMigrationService {

    private final Logger logger = LoggerFactory.getLogger("MongoBatch");

    private final JdbcTemplate jdbcTemplate;

    public void createBooksTables() {
        jdbcTemplate.execute("CREATE TABLE tmp(id bigint auto_increment," +
                " title VARCHAR(50), book_mongo_id VARCHAR(50), author_mongo_id VARCHAR(50)," +
                " genre_mongo_id VARCHAR(50))");
        jdbcTemplate.execute("CREATE TABLE books(id bigint auto_increment," +
                " title VARCHAR(50), author_id VARCHAR(50), genre_id VARCHAR(50))");
    }

    public void fillBookTargetTable() {
        jdbcTemplate.update(
                "INSERT INTO books (title, author_id, genre_id)  " +
                        "SElECT tmp.title, tmp_author.id, tmp_genre.id " +
                        "FROM tmp  " +
                        "LEFT JOIN tmp_author ON tmp_author.mongo_id = tmp.author_mongo_id " +
                        "LEFT JOIN tmp_genre ON tmp_genre.mongo_id = tmp.genre_mongo_id"
        );
        logger.info("====Print books table====");
        List<BookJdbc> bookJdbcs = jdbcTemplate.query("select * from books",
                getBookJdbcRowMapper());
        bookJdbcs.stream().forEach(book -> logger.info(book.toString()));
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
}
