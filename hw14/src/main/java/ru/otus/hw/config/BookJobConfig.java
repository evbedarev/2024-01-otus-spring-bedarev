package ru.otus.hw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
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
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.service.FinalStepService;

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
    private FinalStepService finalStepService;

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
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(jdbcTemplate.getDataSource())
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
    public Step transformBookStep(ItemReader<Book> bookReader, JdbcBatchItemWriter<Book> bookWriter,
                                  ItemProcessor<Book, Book> bookProcessor) {
        return new StepBuilder("transformBookStep", jobRepository)
                .<Book, Book>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(bookWriter)
                .build();
    }

    //AUTHOR
    @Bean
    public MongoPagingItemReader<Author> authorReader(MongoTemplate template) {
        return new MongoPagingItemReaderBuilder<Author>()
                .name("authorReader")
                .template(template)
                .jsonQuery("{}")
                .targetType(Author.class)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemProcessor<Author, Author> authorProcessor() {
        return item -> {
            return item;
        };
    }

    @Bean
    public JdbcBatchItemWriter<Author> authorWriter() {
        return new JdbcBatchItemWriterBuilder<Author>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(jdbcTemplate.getDataSource())
                .itemPreparedStatementSetter(new ItemPreparedStatementSetter<Author>() {
                    @Override
                    public void setValues(Author item, PreparedStatement ps) throws SQLException {
                        ps.setString(1, item.getFullName());
                        ps.setString(2, item.getId());
                    }
                })
                .sql("INSERT INTO tmp_author(full_name, mongo_id) VALUES (?, ?)")
                .build();
    }

    @Bean
    public Step transformAuthorStep(ItemReader<Author> authorReader, JdbcBatchItemWriter<Author> authorWriter,
                                    ItemProcessor<Author, Author> authorProcessor) {
        return new StepBuilder("transformAuthorStep", jobRepository)
                .<Author, Author>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(authorReader)
                .processor(authorProcessor)
                .writer(authorWriter)
                .build();
    }

    //GENRE
    @Bean
    public MongoPagingItemReader<Genre> genreReader(MongoTemplate template) {
        return new MongoPagingItemReaderBuilder<Genre>()
                .name("genreReader")
                .template(template)
                .jsonQuery("{}")
                .targetType(Genre.class)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemProcessor<Genre, Genre> genreProcessor() {
        return item -> {
            return item;
        };
    }

    @Bean
    public JdbcBatchItemWriter<Genre> genreWriter() {
        return new JdbcBatchItemWriterBuilder<Genre>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(jdbcTemplate.getDataSource())
                .itemPreparedStatementSetter(new ItemPreparedStatementSetter<Genre>() {
                    @Override
                    public void setValues(Genre item, PreparedStatement ps) throws SQLException {
                        ps.setString(1, item.getName());
                        ps.setString(2, item.getId());
                    }
                })
                .sql("INSERT INTO tmp_genre(name, mongo_id) VALUES (?, ?)")
                .build();
    }

    @Bean
    public Step transformGenreStep(ItemReader<Genre> genreReader, JdbcBatchItemWriter<Genre> genreWriter,
                                   ItemProcessor<Genre, Genre> genreProcessor) {
        return new StepBuilder("transformGenreStep", jobRepository)
                .<Genre, Genre>chunk(CHUNK_SIZE,platformTransactionManager)
                .reader(genreReader)
                .processor(genreProcessor)
                .writer(genreWriter)
                .build();
    }

    @Bean
    public Job importBookJob(Step transformBookStep,
                             Step transformAuthorStep,
                             Step transformGenreStep,
                             Step finalStep) {
        return new JobBuilder(IMPORT_BOOK_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(transformAuthorStep)
                .next(transformGenreStep)
                .next(transformBookStep)
                .next(finalStep)
                .build();
    }

    @Bean
    public MethodInvokingTaskletAdapter finalStepTasklet() {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();
        adapter.setTargetObject(finalStepService);
        adapter.setTargetMethod("fillTargetTablesAndDropTmp");
        return adapter;
    }

    @Bean
    public Step finalStep() {
        return new StepBuilder("finalStep",jobRepository)
                .tasklet(finalStepTasklet(), platformTransactionManager)
                .build();
    }
}
