package ru.otus.services;

public class SuperSimpleUserAuthService implements UserAuthService {
    private static final String login = "user";
    private static final String password = "pass";

    @Override
    public boolean authenticate(String login, String password) {
        return login.equals(SuperSimpleUserAuthService.login) && password.equals(SuperSimpleUserAuthService.password);
    }
}
