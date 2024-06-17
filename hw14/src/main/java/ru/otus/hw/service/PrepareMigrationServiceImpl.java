package ru.otus.hw.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PrepareMigrationServiceImpl implements PrepareMigrationService {

    private final static String AUTHOR_TMP_TABLE_NAME = "tmp_author";

    private final static String AUTHOR_TABLE_NAME = "authors";

    private final static String GENRE_TMP_TABLE_NAME = "tmp_genre";

    private final static String GENRE_TABLE_NAME = "genres";

    private final static String BOOK_TMP_TABLE_NAME = "tmp";

    private final static String BOOK_TABLE_NAME = "books";

    private final Logger logger = LoggerFactory.getLogger("MongoBatch");

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void prepare() {
        prepareAuthorTables();
        prepareGenreTables();
        prepareBookTables();
    }

    private void prepareAuthorTables() {
        logger.info("Begin Author import job");
        logger.info("Create Author temp table");
        if (checkTableExists(AUTHOR_TMP_TABLE_NAME)) {
            jdbcTemplate.execute("CREATE TABLE %s(id bigint auto_increment,".formatted(AUTHOR_TMP_TABLE_NAME) +
                    " full_name VARCHAR(50), mongo_id VARCHAR(50))");
        }
        logger.info("Create Author target table");

        if (checkTableExists(AUTHOR_TABLE_NAME)) {
            jdbcTemplate.execute("CREATE TABLE %s(id bigint auto_increment,".formatted(AUTHOR_TABLE_NAME) +
                    " full_name VARCHAR(50))");
        }
    }

    private void prepareGenreTables() {
        logger.info("Begin Genre import job");
        logger.info("Create Genre temp table");
        if (checkTableExists(GENRE_TMP_TABLE_NAME)) {
            jdbcTemplate.execute("CREATE TABLE %s(id bigint auto_increment,".formatted(GENRE_TMP_TABLE_NAME) +
                    " name VARCHAR(50), mongo_id VARCHAR(50))");
        }
        logger.info("Create Genre target table");
        if (checkTableExists(GENRE_TABLE_NAME)) {
            jdbcTemplate.execute("CREATE TABLE genres(id bigint auto_increment,".formatted(GENRE_TABLE_NAME) +
                    " name VARCHAR(50))");
        }
    }

    private void prepareBookTables() {
        if (checkTableExists(BOOK_TMP_TABLE_NAME)) {
            jdbcTemplate.execute("CREATE TABLE tmp(id bigint auto_increment," +
                    " title VARCHAR(50), book_mongo_id VARCHAR(50), author_mongo_id VARCHAR(50)," +
                    " genre_mongo_id VARCHAR(50))");
        }
        if (checkTableExists(BOOK_TABLE_NAME)) {
            jdbcTemplate.execute("CREATE TABLE %s(id bigint auto_increment,".formatted(BOOK_TABLE_NAME) +
                    " title VARCHAR(50), author_id VARCHAR(50), genre_id VARCHAR(50))");
        }
    }

    private boolean checkTableExists(String tableName) {
        try {
          jdbcTemplate.execute("select * from %s".formatted(tableName));
        } catch (BadSqlGrammarException ex) {
            if (ex.getSQLException().getMessage().contains("not found"))
                return true;
        }
        logger.warn("TABLE %s Exists".formatted(tableName));
        return false;
    }
}
