package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class BatchCommands {

    private final Job importBookJob;

    private final Job importAuthorJob;

    private final Job importGenreJob;

    private final JobLauncher jobLauncher;

    @ShellMethod(value = "starMigrationFromMongoToH2", key = "mth")
    public void startMigrationFromMongoToFile() throws Exception {
        JobExecution executionAuthor = jobLauncher.run(
                importAuthorJob, new JobParametersBuilder().toJobParameters()
        );
        JobExecution executionGenre = jobLauncher.run(
                importGenreJob, new JobParametersBuilder().toJobParameters()
        );
        JobExecution execution = jobLauncher.run(
                importBookJob, new JobParametersBuilder().toJobParameters()
        );
        System.out.println(execution);
    }
}
