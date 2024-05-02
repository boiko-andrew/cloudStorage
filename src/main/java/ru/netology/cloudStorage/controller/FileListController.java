package ru.netology.cloudStorage.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudStorage.dto.FileDto;
import ru.netology.cloudStorage.entity.User;
import ru.netology.cloudStorage.service.AuthenticationService;
import ru.netology.cloudStorage.service.FileService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/list")
@Validated
public class FileListController {
    private final FileService fileService;
    private final AuthenticationService authenticationService;

    public FileListController(FileService fileService, AuthenticationService authenticationService) {
        this.fileService = fileService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public List<FileDto> getFileList(@RequestHeader("auth-token") @NotBlank String authToken,
                                     @Min(1) int limit) {
        User user = authenticationService.getUserByToken(authToken);
        return fileService.getAllFiles(user, limit);
    }
}