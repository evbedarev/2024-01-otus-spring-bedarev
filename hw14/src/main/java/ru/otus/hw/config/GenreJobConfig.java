package ru.otus.hw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
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
import ru.otus.hw.models.Genre;
import ru.otus.hw.service.GenreMigrationService;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

@Configuration
public class GenreJobConfig {

    public static final String IMPORT_GENRE_JOB_NAME = "importGenreJob";

    private static final int CHUNK_SIZE = 4;

    private final Logger logger = LoggerFactory.getLogger("MongoBatch");

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GenreMigrationService genreMigrationService;

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
                .namedParametersJdbcTemplate(new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource()))
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
    public Job importGenreJob(Step transformGenreStep) {
        return new JobBuilder(IMPORT_GENRE_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(transformGenreStep)
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        genreMigrationService.createGenresTables();
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        genreMigrationService.fillGenreTargetTable();
                    }
                })
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

}
