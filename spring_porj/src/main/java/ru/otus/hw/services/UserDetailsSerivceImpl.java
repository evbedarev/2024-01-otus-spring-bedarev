package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityAlreadyExistsException;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.security.OwnUserPrincipal;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsSerivceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsSerivceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User %s not found".formatted(username));
        } else {
            return new OwnUserPrincipal(user.get());
        }
    }

    @Override
    public User save(User user) {
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            throw new EntityAlreadyExistsException("User with username %s already exists".formatted(user.getUsername()));
        } else {
            logger.info("Create new user: %s".formatted(user.getUsername()));
            return userRepository.save(user);
        }
    }
}
