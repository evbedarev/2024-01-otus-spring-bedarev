package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.User;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldCorrectFindUserByName() {
        Optional<User> user = userRepository.findByUsername("madjo");
        assertThat(user).isPresent().get()
                .hasFieldOrPropertyWithValue("username", "madjo")
                .hasFieldOrPropertyWithValue("role", "ROLE_USER");
    }
}
