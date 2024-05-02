package ru.netology.cloudStorage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudStorage.dto.FileNameDto;
import ru.netology.cloudStorage.entity.User;
import ru.netology.cloudStorage.repository.TokenRepository;
import ru.netology.cloudStorage.repository.UserRepository;
import ru.netology.cloudStorage.service.AuthenticationService;
import ru.netology.cloudStorage.service.FileService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController
@RequestMapping("/file")
@Validated
public class FileController {
    private final AuthenticationService authenticationService;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public FileController(AuthenticationService authenticationService, FileService fileService,
                          UserRepository userRepository, TokenRepository tokenRepository) {
        this.authenticationService = authenticationService;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") @NotBlank String authToken,
                                        @RequestParam("filename") @NotBlank String filename,
                                        @RequestBody @NotNull MultipartFile file) throws IOException {

        User user = authenticationService.getUserByToken(authToken);
        fileService.addFile(filename, file.getBytes(), user);
        return new ResponseEntity("Success upload", HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") @NotBlank String authToken,
                                        @RequestParam("filename") @NotBlank String filename) {

        User user = authenticationService.getUserByToken(authToken);
        fileService.deleteFile(filename, user);
        return new ResponseEntity("Success deleted", HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> renameFile(@RequestHeader("auth-token") @NotBlank String authToken,
                                        @RequestParam("filename") @NotBlank String filename,
                                        @Valid @RequestBody FileNameDto newFilename) {
        User user = authenticationService.getUserByToken(authToken);
        String newName = newFilename.getFilename();
        fileService.renameFile(filename, user, newName);
        return new ResponseEntity("Success renamed", HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getFile(@RequestHeader("auth-token") @NotBlank String authToken,
                          @NotBlank String filename) {
        User user = authenticationService.getUserByToken(authToken);
        return fileService.downloadFile(filename, user);
    }
}