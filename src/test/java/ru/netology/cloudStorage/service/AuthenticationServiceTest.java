package ru.netology.cloudStorage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.cloudStorage.dto.UserDto;
import ru.netology.cloudStorage.entity.User;
import ru.netology.cloudStorage.repository.TokenRepository;
import ru.netology.cloudStorage.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class AuthenticationServiceTest {
    public static final String VALID_AUTH_TOKEN = "Bearer 777";
    public static final String INVALID_AUTH_TOKEN = "123";
    public static final String VALID_LOGIN = "admin@mail.ru";
    public static final String INVALID_LOGIN = "imposter@mail.ru";
    public static final String VALID_PASSWORD = "admin_password";
    public static final String INVALID_PASSWORD = "qwerty";

    private final UserRepository userRepository = createUserRepositoryMock();
    private final TokenRepository tokenRepository = createTokenRepositoryMock();

    private UserRepository createUserRepositoryMock() {
        final UserRepository userRepository = Mockito.mock(UserRepository.class);

        when(userRepository.findUserByLogin(VALID_LOGIN)).
                thenReturn(Optional.of(new User(1L, VALID_LOGIN, VALID_PASSWORD)));
        when(userRepository.findUserByLogin(INVALID_LOGIN))
                .thenReturn(Optional.empty());

        return userRepository;
    }

    private TokenRepository createTokenRepositoryMock() {
        final TokenRepository tokenRepository = Mockito.mock(TokenRepository.class);

        when(tokenRepository.getLoginByToken(VALID_AUTH_TOKEN.split(" ")[1].trim()))
                .thenReturn(Optional.of(VALID_LOGIN));
        when(tokenRepository.getLoginByToken(INVALID_AUTH_TOKEN))
                .thenReturn(Optional.empty());

        return tokenRepository;
    }

    @Test
    void validLoginAndValidPasswordTest() {
        final AuthenticationService authenticationService =
                new AuthenticationService(userRepository, tokenRepository);
        Assertions.assertDoesNotThrow(() -> authenticationService
                .login(new UserDto(VALID_LOGIN, VALID_PASSWORD)));
    }

    @Test
    void invalidLoginAndValidPasswordTest() {
        final AuthenticationService authenticationService =
                new AuthenticationService(userRepository, tokenRepository);
        Assertions.assertThrows(RuntimeException.class, () -> authenticationService
                .login(new UserDto(INVALID_LOGIN, VALID_PASSWORD)));
    }

    @Test
    void validLoginAndInvalidPasswordTest() {
        final AuthenticationService authenticationService =
                new AuthenticationService(userRepository, tokenRepository);
        Assertions.assertThrows(RuntimeException.class, () -> authenticationService
                .login(new UserDto(VALID_LOGIN, INVALID_PASSWORD)));
    }

    @Test
    void logoutTest() {
        final AuthenticationService authenticationService =
                new AuthenticationService(userRepository, tokenRepository);
        Assertions.assertDoesNotThrow(() -> authenticationService
                .logout(VALID_AUTH_TOKEN));
    }

    @Test
    void validTokenTest() {
        final AuthenticationService authenticationService =
                new AuthenticationService(userRepository, tokenRepository);
        Assertions.assertDoesNotThrow(() -> authenticationService
                .getUserByToken(VALID_AUTH_TOKEN));
    }

    @Test
    void invalidTokenTest() {
        final AuthenticationService authenticationService =
                new AuthenticationService(userRepository, tokenRepository);
        Assertions.assertThrows(RuntimeException.class, () -> authenticationService
                .getUserByToken(INVALID_AUTH_TOKEN));
    }
}