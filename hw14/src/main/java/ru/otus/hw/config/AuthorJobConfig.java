package ru.otus.hw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
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
import ru.otus.hw.models.Author;
import ru.otus.hw.service.AuthorMigrationService;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

@Configuration
public class AuthorJobConfig {

    public static final String IMPORT_AUTHOR_JOB_NAME = "importAuthorJob";

    private static final int CHUNK_SIZE = 4;

    private final Logger logger = LoggerFactory.getLogger("MongoBatch");

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuthorMigrationService authorMigrationService;

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
                .namedParametersJdbcTemplate(new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource()))
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
    public Job importAuthorJob(Step transformAuthorStep) {
        return new JobBuilder(IMPORT_AUTHOR_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(transformAuthorStep)
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        authorMigrationService.createAuthorsTables();
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        authorMigrationService.fillAuthorTargetTable();
                    }
                })
                .build();
    }

    @Bean
    public Step transformAuthorStep(ItemReader<Author> authorReader, JdbcBatchItemWriter<Author> authorWriter,
                                    ItemProcessor<Author, Author> authorProcessor) {
        return new StepBuilder("transformAuthorStep", jobRepository)
                .<Author, Author>chunk(CHUNK_SIZE,platformTransactionManager)
                .reader(authorReader)
                .processor(authorProcessor)
                .writer(authorWriter)
                .listener(new ItemReadListener<Author>() {
                    @Override
                    public void afterRead(Author item) {
                        logger.info("End Read");
                    }
                })
                .listener(new ItemWriteListener<Author>() {
                    @Override
                    public void afterWrite(Chunk<? extends Author> items) {
                        logger.info("End Write");
                    }
                })
                .build();
    }


}
