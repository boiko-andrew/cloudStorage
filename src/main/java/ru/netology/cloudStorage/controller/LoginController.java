package ru.netology.cloudStorage.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudStorage.dto.TokenDto;
import ru.netology.cloudStorage.dto.UserDto;
import ru.netology.cloudStorage.service.AuthenticationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<TokenDto> login(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(authenticationService.login(userDto));
    }
}