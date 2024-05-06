package ru.netology.cloudStorage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.cloudStorage.entity.File;
import ru.netology.cloudStorage.entity.User;
import ru.netology.cloudStorage.exception.DuplicateFileNameException;
import ru.netology.cloudStorage.exception.FileNotFoundException;
import ru.netology.cloudStorage.repository.FileRepository;

import java.util.List;

@Slf4j
@Service
public class FileService {
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public synchronized void addFile(String name, byte[] content, User user) {
        log.info("User id: {}", user.getId());
        fileRepository.save(new File(name, content, user));
    }

    public synchronized void deleteFile(String name, User user) {
        log.info("User id: {}", user.getId());
        List<Long> ids = fileRepository.findFilesByUserIdAndName(user.getId(), name);
        if (ids.isEmpty()) {
            log.info("File with name: {} is not found for user with login: {}", name, user.getLogin());
            throw new FileNotFoundException("File is not found");
        }

        for (Long id : ids) {
            log.info("File with id: {} will be deleted", id);
            fileRepository.deleteById(id);
        }
    }

    public List<File> getAllFiles(User user, int limit) {
        log.info("User id: {}", user.getId());
        return fileRepository.findFilesByUserIdWithLimit(user.getId(), limit);
    }

    public File getFile(String name, User user) {
        List<Long> ids = fileRepository.findFilesByUserIdAndName(user.getId(), name);
        if (ids.isEmpty()) {
            log.info("File with name: {} is not found for user with login: {}", name, user.getLogin());
            throw new FileNotFoundException("File is not found");
        }

        return fileRepository.findFilesById(ids.get(0));
    }

    public synchronized void renameFile(String oldName, User user, String newName) {
        List<Long> ids = fileRepository.findFilesByUserIdAndName(user.getId(), oldName);
        if (ids.isEmpty()) {
            log.info("File with name: {} is not found for user with login: {}", oldName, user.getLogin());
            throw new FileNotFoundException("File is not found");
        }

        List<Long> newIds = fileRepository.findFilesByUserIdAndName(user.getId(), newName);
        if (!newIds.isEmpty()) {
            log.info("User with login: {} already has file with name: {}", user.getLogin(), newName);
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
            log.info("File with name: {} is not found for user with login: {}", filename, user.getLogin());
            throw new FileNotFoundException("File is not found");
        }

        final File file = this.getFile(filename, user);
        log.info("Download content for file with name: {}", filename);
        return file.getContent();
    }
}