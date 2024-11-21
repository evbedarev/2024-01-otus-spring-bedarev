package ru.otus.hw.rest;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.stylesheets.LinkStyle;
import ru.otus.hw.dto.user.UserDto;
import ru.otus.hw.exceptions.EntityAlreadyExistsException;
import ru.otus.hw.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class UserRestController {
    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    public UserRestController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @GetMapping("/api/v1/user")
    public Set<String> getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .map(r -> r.getAuthority()).collect(Collectors.toSet());
    }

    @PostMapping("/api/v1/user")
    public UserDto saveNewUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errUser = "";
            String errPass = "";
            if (bindingResult.getFieldError("username") != null) {
                errUser = bindingResult.getFieldError("username").getDefaultMessage();
            }
            if (bindingResult.getFieldError("password") != null) {
                errPass = bindingResult.getFieldError("password").getDefaultMessage();
            }
            return new UserDto().getDtoWithError(errUser, errPass);
        }
        try {
            userDto.setRole("ROLE_USER");
            userService.save(userDto.toUser(passwordEncoder));
            return new UserDto();
        } catch (EntityAlreadyExistsException exception) {
            return new UserDto().getDtoWithError(exception.getMessage(), "");
        }
    }
}
