package ru.otus.hw.security;

import org.springframework.stereotype.Component;

@Component
public class InMemoryLoginContext implements LoginContext {
    private String userName = "";

    @Override
    public void login(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean isUserLoggedIn() {
        return !userName.equals("");
    }

    @Override
    public String getLogin() {
        return userName;
    }
}
