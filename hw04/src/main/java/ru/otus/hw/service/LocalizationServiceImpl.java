package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.LocaleConfig;


@RequiredArgsConstructor
@Service
public class LocalizationServiceImpl implements LocalizationService {

    private final LocaleConfig localeConfig;

    private final MessageSource messageSource;

    @Override
    public String getMessage(String prop, Object... args) {
        return messageSource.getMessage(prop, args, localeConfig.getLocale());
    }

    @Override
    public String getMessage(String prop) {
        return messageSource.getMessage(prop, null, localeConfig.getLocale());
    }
}
