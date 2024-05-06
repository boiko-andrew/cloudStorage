package ru.netology.cloudStorage.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudStorage.dto.TokenDto;
import ru.netology.cloudStorage.dto.UserDto;
import ru.netology.cloudStorage.entity.User;
import ru.netology.cloudStorage.service.AuthenticationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {
    private final AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<TokenDto> login(@Valid @RequestBody UserDto userDto) {
        final Long id = 1L;
        final String login = userDto.getLogin();
        final String password = userDto.getPassword();
        User user = new User(id, login, password);

        log.info("User with login: {} tries to sign in", login);
        final String token = authenticationService.login(user);
        final TokenDto tokenDto = new TokenDto(token);
        log.info("User with login: {} successfully signed in with auth-token: {}", login, token);

        return ResponseEntity.ok(tokenDto);
    }
}