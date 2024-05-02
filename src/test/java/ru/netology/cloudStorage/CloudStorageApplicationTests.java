package ru.netology.cloudStorage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.netology.cloudStorage.dto.FileNameDto;
import ru.netology.cloudStorage.dto.TokenDto;
import ru.netology.cloudStorage.dto.UserDto;
//import ru.netology.cloudservice.dto.LoginInResponse;
import ru.netology.cloudStorage.entity.File;
//import ru.netology.cloudservice.entities.TokenEntity;
import ru.netology.cloudStorage.entity.User;
import ru.netology.cloudStorage.repository.FileRepository;
import ru.netology.cloudStorage.repository.TokenRepository;
import ru.netology.cloudStorage.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(locations = "/test.properties")
class CloudStorageApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    TokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        fileRepository.deleteAll();
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    public void loginTest() {
        userRepository.save(new User(1L, "admin@mail.ru", "admin_password"));
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        final UserDto operation = new UserDto("admin@mail.ru", "admin_password");
        final HttpEntity<UserDto> request = new HttpEntity<>(operation, headers);
        final ResponseEntity<String> result = this.restTemplate.postForEntity("/login", request, String.class);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getBody());
    }

    @Test
    public void logoutTest() {
        final String authToken = "Bearer 777";
        tokenRepository.putTokenAndLogin(authToken, "admin@mail.ru");

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);
        final HttpEntity<Void> request = new HttpEntity<>(null, headers);

        this.restTemplate.postForEntity("/logout", request, Void.class);

        Assertions.assertFalse(tokenRepository
                .getLoginByToken(authToken.split(" ")[1].trim()).isPresent());
    }

    @Test
    public void addFileTest() {
        userRepository.save(new User(1L, "admin@mail.ru", "admin_password"));

        final String authToken = "Bearer 777";
        tokenRepository.putTokenAndLogin(authToken.split(" ")[1].trim(), "admin@mail.ru");

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);

        final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("testfile_01.txt"));

        final HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(parts, headers);

        this.restTemplate.postForEntity("/file?auth-token=" + authToken + "&filename=testfile_01.txt",
                request, Void.class);

        Optional<User> user = userRepository.findUserByLogin("admin@mail.ru");
        if (user.isPresent()) {
            Long userId = user.get().getId();
            final List<Long> fileIds = fileRepository
                    .findFilesByUserIdAndName(userId, "testfile_01.txt");
            Assertions.assertFalse(fileIds.isEmpty());
        }
    }


    @Test
    public void deleteFileTest() {
        userRepository.save(new User(1L, "admin@mail.ru", "admin_password"));

        final String authToken = "Bearer 777";
        tokenRepository.putTokenAndLogin(authToken.split(" ")[1].trim(), "admin@mail.ru");

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);

        // First add file
        final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("testfile_01.txt"));

        final HttpEntity<MultiValueMap<String, Object>> addRequest = new HttpEntity<>(parts, headers);

        this.restTemplate.postForEntity("/file?auth-token=" + authToken + "&filename=testfile_01.txt",
                addRequest, Void.class);

        // Then delete file
        final HttpEntity<Void> deleteRequest = new HttpEntity<>(null, headers);
        this.restTemplate.exchange("/file?auth-token=" + authToken + "&filename=testfile_01.txt",
                HttpMethod.DELETE, deleteRequest, Void.class);

        Optional<User> user = userRepository.findUserByLogin("admin@mail.ru");
        if (user.isPresent()) {
            Long userId = user.get().getId();
            final List<Long> fileIds = fileRepository
                    .findFilesByUserIdAndName(userId, "testfile_01.txt");
            Assertions.assertTrue(fileIds.isEmpty());
        }
    }

    @Test
    public void getFileTest() {
        User user = new User(1L, "admin@mail.ru", "admin_password");
        userRepository.save(user);

        user.setId(userRepository.findUserByLogin("admin@mail.ru").get().getId());

        final String authToken = "Bearer 777";
        tokenRepository.putTokenAndLogin(authToken.split(" ")[1].trim(), "admin@mail.ru");

        fileRepository.save(new File("testfile_05.txt", new byte[]{49, 51, 50}, user));

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);

        final HttpEntity<Void> request = new HttpEntity<>(null, headers);

        final ResponseEntity<byte[]> result = this.restTemplate.exchange(
                "/file?auth-token=" + authToken + "&filename=testfile_05.txt",
                HttpMethod.GET, request, byte[].class);

        Assertions.assertNotNull(result.getBody());
        Assertions.assertArrayEquals(new byte[]{49, 51, 50}, result.getBody());
    }

    @Test
    public void renameFileTest() {
        User user = new User(1L, "admin@mail.ru", "admin_password");
        userRepository.save(user);

        user.setId(userRepository.findUserByLogin("admin@mail.ru").get().getId());

        final String authToken = "Bearer 777";
        tokenRepository.putTokenAndLogin(authToken.split(" ")[1].trim(), "admin@mail.ru");

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);

        // First add file
        final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("testfile_01.txt"));

        final HttpEntity<MultiValueMap<String, Object>> addRequest = new HttpEntity<>(parts, headers);

        this.restTemplate.postForEntity("/file?auth-token=" + authToken + "&filename=testfile_01.txt",
                addRequest, Void.class);

        // Then rename file
        final HttpEntity<FileNameDto> request =
                new HttpEntity<>(new FileNameDto("testfile_10.txt"), headers);

        this.restTemplate.exchange("/file?filename=testfile_01.txt",
                HttpMethod.PUT, request, Void.class);

        List<Long> initialFileIds = fileRepository.findFilesByUserIdAndName(user.getId(),
                "testfile_01.txt");
        Assertions.assertTrue(initialFileIds.isEmpty());

        List<Long> renamedFileIds = fileRepository.findFilesByUserIdAndName(user.getId(),
                "testfile_10.txt");
        Assertions.assertFalse(renamedFileIds.isEmpty());
    }


    @Test
    public void getFileListTest() {
        User user = new User(1L, "admin@mail.ru", "admin_password");
        userRepository.save(user);

        user.setId(userRepository.findUserByLogin("admin@mail.ru").get().getId());

        final String authToken = "Bearer 777";
        tokenRepository.putTokenAndLogin(authToken.split(" ")[1].trim(), "admin@mail.ru");

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);

        // Add first file
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("testfile_01.txt"));

        HttpEntity<MultiValueMap<String, Object>> addRequest = new HttpEntity<>(parts, headers);

        this.restTemplate.postForEntity("/file?auth-token=" + authToken + "&filename=testfile_01.txt",
                addRequest, Void.class);

        // Add second file
        parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("testfile_02.txt"));

        addRequest = new HttpEntity<>(parts, headers);

        this.restTemplate.postForEntity("/file?auth-token=" + authToken + "&filename=testfile_02.txt",
                addRequest, Void.class);

        // Add third file
        parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("testfile_03.txt"));

        addRequest = new HttpEntity<>(parts, headers);

        this.restTemplate.postForEntity("/file?auth-token=" + authToken + "&filename=testfile_03.txt",
                addRequest, Void.class);

        final HttpEntity<Void> request = new HttpEntity<>(null, headers);

        final ResponseEntity<Object> result = this.restTemplate.exchange("/list?limit=10",
                HttpMethod.GET, request, Object.class);

        Assertions.assertNotNull(result.getBody());
    }
}