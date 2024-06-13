package ru.otus.hw.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@Service
public class GenreMigrationServiceImpl implements GenreMigrationService {

    private final JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger("MongoBatch");

    public void createGenresTables() {
        logger.info("Begin Genre import job");
        logger.info("Create Genre temp table");
        jdbcTemplate.execute("CREATE TABLE tmp_genre(id bigint auto_increment," +
                " name VARCHAR(50), mongo_id VARCHAR(50))");
        logger.info("Create Genre target table");
        jdbcTemplate.execute("CREATE TABLE genres(id bigint auto_increment," +
                " name VARCHAR(50))");
    }

    public void fillGenreTargetTable() {
        logger.info("Fill target author table");
        jdbcTemplate.execute("INSERT INTO genres (name) " +
                "SELECT name FROM tmp_genre");
        logger.info("====Print genres table====");
        List<Genre> genres = jdbcTemplate.query("select * from genres", getGenreRowMapper());
        genres.stream().forEach(genre -> logger.info(genre.toString()));
    }

    public static RowMapper<Genre> getGenreRowMapper() {
        return new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Genre(rs.getString(1), rs.getString(2));
            }
        };
    }
}
