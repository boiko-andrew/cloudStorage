package ru.netology.cloudStorage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.cloudStorage.dto.FileDto;
import ru.netology.cloudStorage.entity.File;
import ru.netology.cloudStorage.entity.User;
import ru.netology.cloudStorage.repository.FileRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class FileServiceTest {
    public static final String EXISTING_FILE_NAME = "existingFile";
    public static final String NON_EXISTING_FILE_NAME = "nonExistingFile";

    private final User existingUser = new User(1L, "admin@mail.ru", "admin_password");
    private final File existingFile = new File(1L, EXISTING_FILE_NAME, new byte[]{0, 1, 2}, existingUser);
    private final Long existingFileId = Long.parseLong(existingFile.getId().toString());
    private final Long nonExistingFileId = 100L;


    private final FileRepository fileRepository = createFileRepositoryMock();
    private final FileService fileService = new FileService(fileRepository);

    private FileRepository createFileRepositoryMock() {
        final FileRepository fileRepository = Mockito.mock(FileRepository.class);

        ArrayList<Long> existingFileIds = new ArrayList<>();
        existingFileIds.add(existingFileId);

        ArrayList<Long> nonExistingFileIds = new ArrayList<>();

        when(fileRepository.findFilesByUserIdAndName(existingUser.getId(), EXISTING_FILE_NAME))
                .thenReturn(existingFileIds);

        when(fileRepository.findFilesByUserIdAndName(existingUser.getId(), NON_EXISTING_FILE_NAME))
                .thenReturn(nonExistingFileIds);

        when(fileRepository.findFilesById(existingFileId))
                .thenReturn(existingFile);

        when(fileRepository.findFilesById(nonExistingFileId))
                .thenReturn(null);

        when(fileRepository.findFilesByUserIdWithLimit(existingUser.getId(), 1))
                .thenReturn(List.of(existingFile));

        return fileRepository;
    }

    @Test
    void getExistingFileTest() {
        final byte[] expectedFile = new byte[]{0, 1, 2};
        final byte[] file = fileService.getFile(EXISTING_FILE_NAME, existingUser).getContent();
        Assertions.assertArrayEquals(expectedFile, file);
    }

    @Test
    void getNonExistingFileTest() {
        Assertions.assertThrows(RuntimeException.class,
                () -> fileService.getFile(NON_EXISTING_FILE_NAME, existingUser));
    }

    @Test
    void deleteExistingFileTest() {
        Assertions.assertDoesNotThrow(() -> fileService.deleteFile(EXISTING_FILE_NAME, existingUser));
    }

    @Test
    void deleteNonExistingFileTest() {
        Assertions.assertThrows(RuntimeException.class,
                () -> fileService.deleteFile(NON_EXISTING_FILE_NAME, existingUser));
    }

    @Test
    void renameExistingFileTest() {
        Assertions.assertDoesNotThrow(() -> fileService.renameFile(EXISTING_FILE_NAME,
                existingUser, NON_EXISTING_FILE_NAME));
    }

    @Test
    void renameNonExistingFileTest() {
        Assertions.assertThrows(RuntimeException.class,
                () -> fileService.renameFile(NON_EXISTING_FILE_NAME,
                        existingUser, EXISTING_FILE_NAME));
    }

    @Test
    void getFileList() {
        final List<FileDto> expectedFileList = List.of(new FileDto(EXISTING_FILE_NAME, 3));
        final List<FileDto> actualFileList = fileService.getAllFiles(existingUser, 1);
        Assertions.assertEquals(expectedFileList, actualFileList);
    }
}