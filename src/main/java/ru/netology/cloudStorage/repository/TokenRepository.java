package ru.netology.cloudStorage.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TokenRepository {
    private final Map<String, String> tokensAndLogins = new ConcurrentHashMap<>();

    public void putTokenAndLogin(String token, String login) {
        tokensAndLogins.put(token, login);
    }

    public void removeTokenAndLoginByToken(String token) {
        tokensAndLogins.remove(token);
    }

    public Optional<String> getLoginByToken(String token) {
        return Optional.ofNullable(tokensAndLogins.get(token));
    }

    public void deleteAll() {
        this.tokensAndLogins.clear();
    }
}