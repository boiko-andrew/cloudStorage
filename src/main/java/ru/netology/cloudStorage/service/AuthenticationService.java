package ru.netology.cloudStorage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.cloudStorage.entity.User;
import ru.netology.cloudStorage.exception.AuthorizationException;
import ru.netology.cloudStorage.exception.BadCredentialsException;
import ru.netology.cloudStorage.repository.TokenRepository;
import ru.netology.cloudStorage.repository.UserRepository;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final Random random = new Random();

    public AuthenticationService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public String login(User user) {
        final String login = user.getLogin();
        final String password = user.getPassword();

        log.info("Try to find user with login: {} in database", login);

        final Optional<User> repositoryUser = userRepository.findUserByLogin(login);
        if (repositoryUser.isEmpty()) {
            log.info("User with login: {} is not found", login);
            throw new BadCredentialsException("User with login " + login + " is not found");
        }
        log.info("User with login: {} is found in database", login);

        if (!repositoryUser.get().getPassword().equals(password)) {
            log.info("User with login: {} provided incorrect password", login);
            throw new BadCredentialsException("Incorrect password for user " + login);
        }

        final String token = String.valueOf(random.nextLong());
        tokenRepository.putTokenAndLogin(token, login);

        return token;
    }

    public void logout(String authToken) {
        tokenRepository.removeTokenAndLoginByToken(authToken);
        log.info("Auth-token: {} was removed from hash map", authToken);
    }

    public User getUserByToken(String authToken) {
        String authTokenWithoutBearer;
        String[] authTokenParts = authToken.split(" ");
        if (authTokenParts.length >= 2) {
            authTokenWithoutBearer = authTokenParts[1];
        } else {
            authTokenWithoutBearer = authToken;
        }


        log.info("Auth-token: {}, auth-token without bearer: {}", authToken, authTokenWithoutBearer);
        log.info("Try to find auth-token: {} in hash map", authTokenWithoutBearer);

        final Optional<String> login = tokenRepository.getLoginByToken(authTokenWithoutBearer);
        if (login.isEmpty()) {
            log.info("Auth-token: {} is not found in hash map", authTokenWithoutBearer);
            throw new AuthorizationException("User is not authorized");
        }

        log.info("Auth-token: {} is found in hash map", authTokenWithoutBearer);
        log.info("User login: {}", login.get());

        final Optional<User> user = userRepository.findUserByLogin(login.get());
        if (user.isEmpty()) {
            log.info("Internal error. User with login: {} is not found in database", login.get());
            throw new AuthorizationException("User is not found");
        }

        return user.get();
    }
}