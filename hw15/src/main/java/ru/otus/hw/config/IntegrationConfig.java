package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.*;
import org.springframework.integration.scheduling.PollerMetadata;
import ru.otus.hw.models.BookInRead;
import ru.otus.hw.models.FinishedBook;
import ru.otus.hw.service.LibraryService;

@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannelSpec<?, ?> booksChannel() {
        return MessageChannels.queue(10);
    }

    @Bean
    public MessageChannelSpec<?, ?> issuingBooksChannel() {
        return MessageChannels.publishSubscribe();
    }
    @Bean
    public MessageChannelSpec<?, ?> finishedBookChannel() {
        return MessageChannels.publishSubscribe();
    }
    @Bean
    public MessageChannelSpec<?, ?> unfinishedBookChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerSpec poller() {
        return Pollers.fixedRate(100).maxMessagesPerPoll(2);
    }

    @Bean
    public IntegrationFlow libraryFlow(LibraryService libraryService) {
        return IntegrationFlow.from(booksChannel())
                .split()
                .handle(libraryService, "issueBook")
                .<BookInRead, BookInRead>transform(b -> new BookInRead(b.getBook(), true,
                        b.getCurrentPage() + 10))
                .route(BookInRead.class, book -> {
                    if (book.getCurrentPage() > book.getBook().getPages()) {
                        return "finishedBookChannel";
                    } else {
                        return "unfinishedBookChannel";
                    }
                })
                .get();
    }

    @Bean
    public IntegrationFlow finishedBookFlow(LibraryService libraryService) {
        return IntegrationFlow.from(finishedBookChannel())
                .handle(libraryService, "finish")
                .<FinishedBook, BookInRead>transform(b -> new BookInRead(b.getBook(),false, 0))
                .aggregate()
                .channel(issuingBooksChannel())
                .get();
    }

    @Bean
    public IntegrationFlow unfinishedBookFlow(LibraryService libraryService) {
        return IntegrationFlow.from(unfinishedBookChannel())
                .channel(issuingBooksChannel())
                .aggregate()
                .get();
    }
}
