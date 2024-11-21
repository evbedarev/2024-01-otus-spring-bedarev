package ru.otus.hw.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity (
        securedEnabled = true,
        prePostEnabled = true
)
public class MethodSecurityConfiguration {
}
