package ru.otus.hw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.service.BookMigrationService;

//import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

@Configuration
public class BookJobConfig {

    public static final String IMPORT_BOOK_JOB_NAME = "importBookJob";

    private static final int CHUNK_SIZE = 4;

    private final Logger logger = LoggerFactory.getLogger("MongoBatch");

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BookMigrationService bookMigrationService;

    @Bean
    public MongoPagingItemReader<Book> bookReader(MongoTemplate template) {
       return new MongoPagingItemReaderBuilder<Book>()
               .name("bookReader")
               .template(template)
               .jsonQuery("{}")
               .targetType(Book.class)
               .sorts(new HashMap<>())
               .build();
    }

    @Bean
    public ItemProcessor<Book, Book> bookProcessor() {
        return item -> {
            return item;
        };
    }

    @Bean
    public JdbcBatchItemWriter<Book> bookWriter() {
        return new JdbcBatchItemWriterBuilder<Book>()
                .namedParametersJdbcTemplate(new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource()))
                .itemPreparedStatementSetter(new ItemPreparedStatementSetter<Book>() {
                    @Override
                    public void setValues(Book item, PreparedStatement ps) throws SQLException {
                        ps.setString(1, item.getTitle());
                        ps.setString(2, item.getId());
                        ps.setString(3, item.getAuthor().getId());
                        ps.setString(4, item.getGenre().getId());
                    }
                })
                .sql("INSERT INTO tmp" +
                        "(title, book_mongo_id, author_mongo_id, genre_mongo_id) VALUES (?, ?, ?, ?)")
                .build();
    }

    @Bean
    public Job importBookJob(Step transformBookStep) {
        return new JobBuilder(IMPORT_BOOK_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(transformBookStep)
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        logger.info("Begin job");
                        bookMigrationService.createBooksTables();
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        logger.info("End job");
                        bookMigrationService.fillBookTargetTable();
                        logger.info("====Remove temp tables====");
                        jdbcTemplate.update("DROP TABLE tmp_author, tmp_genre, tmp");
                    }
                })
                .build();
    }

    @Bean
    public Step transformBookStep(ItemReader<Book> bookReader, JdbcBatchItemWriter<Book> bookWriter,
                                  ItemProcessor<Book, Book> bookProcessor) {
        return new StepBuilder("transformBookStep", jobRepository)
                .<Book, Book>chunk(CHUNK_SIZE,platformTransactionManager)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(bookWriter)
                .build();
    }

}
