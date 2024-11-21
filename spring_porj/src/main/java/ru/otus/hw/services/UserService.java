package ru.otus.hw.services;

import ru.otus.hw.dto.user.UserDto;
import ru.otus.hw.models.User;

public interface UserService {
    User save(User user);
}
