package ru.netology.cloudStorage.service;

import org.springframework.stereotype.Service;
import ru.netology.cloudStorage.dto.FileDto;
import ru.netology.cloudStorage.entity.File;
import ru.netology.cloudStorage.entity.User;
import ru.netology.cloudStorage.exception.AuthorizationException;
import ru.netology.cloudStorage.exception.DuplicateFileNameException;
import ru.netology.cloudStorage.exception.FileNotFoundException;
import ru.netology.cloudStorage.repository.FileRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public synchronized void addFile(String name, byte[] content, User user) {
        fileRepository.save(new File(name, content, user));
    }

    public synchronized void deleteFile(String name, User user) {
        List<Long> ids = fileRepository.findFilesByUserIdAndName(user.getId(), name);
        if (ids.isEmpty()) {
            throw new FileNotFoundException("File is not found");
        }

        for (Long id : ids) {
            fileRepository.deleteById(id);
        }
    }

    public List<FileDto> getAllFiles(User user, int limit) {
        Long userId = user.getId();

        List<File> filesList = fileRepository.findFilesByUserIdWithLimit(userId, limit);
        return filesList.stream()
                .map(file -> new FileDto(file.getName(), file.getContent().length))
                .collect(Collectors.toList());
    }

    public File getFile(String name, User user) {
        List<Long> ids = fileRepository.findFilesByUserIdAndName(user.getId(), name);
        if (ids.isEmpty()) {
            throw new FileNotFoundException("File is not found");
        }

        return fileRepository.findFilesById(ids.get(0));
    }

    public synchronized void renameFile(String oldName, User user, String newName) {
        List<Long> ids = fileRepository.findFilesByUserIdAndName(user.getId(), oldName);
        if (ids.isEmpty()) {
            throw new FileNotFoundException("File is not found");
        }

        List<Long> newIds = fileRepository.findFilesByUserIdAndName(user.getId(), newName);
        if (!newIds.isEmpty()) {
            throw new DuplicateFileNameException("User with id = " + user.getId() +
                    " already has file with name " + newName);
        }

        File file = this.getFile(oldName, user);
        file.setName(newName);
        fileRepository.save(file);
    }

    public byte[] downloadFile(String filename, User user) {
        List<Long> ids = fileRepository.findFilesByUserIdAndName(user.getId(), filename);
        if (ids.isEmpty()) {
            throw new FileNotFoundException("File is not found");
        }

        final File file = this.getFile(filename, user);
        return file.getContent();
    }
}