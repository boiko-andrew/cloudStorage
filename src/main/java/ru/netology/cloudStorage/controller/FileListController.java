package ru.netology.cloudStorage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudStorage.dto.FileDto;
import ru.netology.cloudStorage.entity.File;
import ru.netology.cloudStorage.entity.User;
import ru.netology.cloudStorage.service.AuthenticationService;
import ru.netology.cloudStorage.service.FileService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

        log.info("Try to download files list. List limit: {}", limit);
        User user = authenticationService.getUserByToken(authToken);
        List<File> filesList = fileService.getAllFiles(user, limit);
        log.info("User with login: {} downloaded files list. Limit: {}", user.getLogin(), limit);
        return filesList.stream()
                .map(file -> new FileDto(file.getName(), file.getContent().length))
                .collect(Collectors.toList());
    }
}