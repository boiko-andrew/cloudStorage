package ru.netology.cloudStorage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudStorage.service.AuthenticationService;

import javax.validation.constraints.NotBlank;

@Slf4j
@RestController
@RequestMapping("/logout")
@Validated
public class LogoutController {
    private final AuthenticationService authenticationService;

    public LogoutController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<?> logout(@RequestHeader("auth-token") @NotBlank String authToken) {
        log.info("User with auth-token: {} tries to log off", authToken);
        authenticationService.logout(authToken);
        log.info("User with auth-token: {} successfully logged off", authToken);
        return new ResponseEntity("Success logout", HttpStatus.OK);
    }
}