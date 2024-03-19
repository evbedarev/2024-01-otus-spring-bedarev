package ru.otus.hw.security;

public interface LoginContext {

    void login(String username);

    boolean isUserLoggedIn();

    String getLogin();

}
