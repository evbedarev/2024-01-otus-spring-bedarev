package ru.otus.hw.site;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.otus.hw.dto.user.UserDto;
import ru.otus.hw.exceptions.EntityAlreadyExistsException;
import ru.otus.hw.services.UserService;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String registerForm() {
        return "registration";
    }

    @PostMapping
    public String processRegistration(UserDto userDto) {
        try {
            userService.save(userDto.toUser(passwordEncoder));
        } catch (EntityAlreadyExistsException exception) {
            return "user_exists";
        }
        return "redirect:/login";
    }
}
