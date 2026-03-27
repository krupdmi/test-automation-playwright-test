package org.testautomation.commons.dbservices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.testautomation.commons.entities.UserEntity;
import org.testautomation.commons.repository.UserRepository;

import java.util.UUID;

/**
 * DB-level helper for user queries in tests.
 * Use this instead of raw SQL when assertions need verified DB state.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDbService {

    private final UserRepository userRepository;

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found in DB: " + username));
    }

    public UserEntity findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found in DB: " + id));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void deleteByUsername(String username) {
        userRepository.findByUsername(username).ifPresent(u -> {
            userRepository.delete(u);
            log.info("Deleted test user '{}' from DB", username);
        });
    }
}
