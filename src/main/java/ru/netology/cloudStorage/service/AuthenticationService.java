package ru.netology.cloudStorage.service;

import org.springframework.stereotype.Service;
import ru.netology.cloudStorage.dto.TokenDto;
import ru.netology.cloudStorage.dto.UserDto;
import ru.netology.cloudStorage.entity.User;
import ru.netology.cloudStorage.exception.AuthorizationException;
import ru.netology.cloudStorage.exception.BadCredentialsException;
import ru.netology.cloudStorage.repository.TokenRepository;
import ru.netology.cloudStorage.repository.UserRepository;

import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final Random random = new Random();

    public AuthenticationService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public TokenDto login(UserDto userDto) {

        final String login = userDto.getLogin();
        final String password = userDto.getPassword();

        System.out.println("\n");
        System.out.println("In AuthenticationService, login method now");
        System.out.println("User login is: " + login);
        System.out.println("User password is: " + password);


        final User user = userRepository.findUserByLogin(login).orElseThrow(() ->
                new BadCredentialsException("User with login " + login + " not found"));

        if (!user.getPassword().equals(password)) {
            throw new BadCredentialsException("Incorrect password for user " + login);
        }

        final String token = String.valueOf(random.nextLong());
        tokenRepository.putTokenAndLogin(token, login);

        System.out.println("User token is: " + token);
        System.out.println("\n");

        return new TokenDto(token);
    }

    public void logout(String authToken) {
        tokenRepository.removeTokenAndLoginByToken(authToken);
    }

    public User getUserByToken(String authToken) {
        final String authTokenWithoutBearer = authToken.split(" ")[1];

        System.out.println("\n");
        System.out.println("In AuthenticationService, getUserByToken method now");
        System.out.println("User token is: " + authToken);
        System.out.println("User token without bearer is: " + authTokenWithoutBearer);
        System.out.println("\n");

        final Optional<String> login = tokenRepository.getLoginByToken(authTokenWithoutBearer);
        if (login.isEmpty()) {
            throw new AuthorizationException("User is not authorized");
        }

        final Optional<User> user = userRepository.findUserByLogin(login.get());
        if (user.isEmpty()) {
            throw new AuthorizationException("User is not found");
        }

        return user.get();
    }
}