package ru.otus.hw.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@Service
public class AuthorMigrationServiceImpl implements AuthorMigrationService {

    private final Logger logger = LoggerFactory.getLogger("MongoBatch");

    private final JdbcTemplate jdbcTemplate;

    public void createAuthorsTables() {
        logger.info("Begin Author import job");
        logger.info("Create Author temp table");
        jdbcTemplate.execute("CREATE TABLE tmp_author(id bigint auto_increment," +
                " full_name VARCHAR(50), mongo_id VARCHAR(50))");
        logger.info("Create Author target table");
        jdbcTemplate.execute("CREATE TABLE authors(id bigint auto_increment," +
                " full_name VARCHAR(50))");
    }

    public void fillAuthorTargetTable() {
        logger.info("Fill target author table");
        jdbcTemplate.execute("INSERT INTO authors (full_name) " +
                "SELECT full_name FROM tmp_author");
        logger.info("====Print authors table====");
        List<Author> authors = jdbcTemplate.query("select * from authors", getAuthorRowMapper());
        authors.stream().forEach(author -> logger.info(author.toString()));
    }

    public static RowMapper<Author> getAuthorRowMapper() {
        return new RowMapper<Author>() {
            @Override
            public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Author(rs.getString(1), rs.getString(2));
            }
        };
    }
}
