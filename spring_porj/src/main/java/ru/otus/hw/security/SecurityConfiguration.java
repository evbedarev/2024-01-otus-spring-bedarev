package ru.otus.hw.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authorizeHttpRequests(authorize -> authorize
                        //.requestMatchers("/login").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/user").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/books/find/title").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/v1/books/find/author").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/text").hasRole("ADMIN")
                        .requestMatchers("/text/*").hasRole("ADMIN")
                        .requestMatchers("/text_user").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/text_user/*").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/books").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/books/*").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/authors").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/genres").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/books/pages/*").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/text/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/text/*").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/v1/bookmarks").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/bookmarks/*").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/api/v1/pages/**").hasRole("ADMIN")
                        .requestMatchers("/**").hasRole("ADMIN")
                );
        http.formLogin(auth -> auth.loginPage("/login").permitAll());
                //.formLogin().loginPage("login").loginProcessingUrl("/auth").permitAll();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
