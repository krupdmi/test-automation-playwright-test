package org.testautomation.commons.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.testautomation.commons.entities.UserEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link UserEntity}.
 * Add project-specific query methods here.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByUsername(String username);
}
