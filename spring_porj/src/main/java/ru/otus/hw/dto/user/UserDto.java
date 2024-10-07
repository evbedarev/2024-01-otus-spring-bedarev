package ru.otus.hw.dto.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.otus.hw.models.User;

@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Getter
    @Setter
    @Size(min = 4, message = "input more then 4 chars")
    private String username;

    @Size(min = 8, message = "input more then 8 chars")
    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String role;

    @Getter
    @Setter
    private boolean hasError = false;

    @Getter
    @Setter
    private String errorPass;

    @Getter
    @Setter
    private String errorUser;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(0, username, passwordEncoder.encode(password), role);
    }

    public UserDto getDtoWithError( String errorUser, String errorPass) {
        UserDto userDto = new UserDto();
        userDto.setHasError(true);
        userDto.setErrorPass(errorPass);
        userDto.setErrorUser(errorUser);
        return userDto;
    }
}
