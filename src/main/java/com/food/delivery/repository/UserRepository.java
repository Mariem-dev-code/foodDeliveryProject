package com.food.delivery.repository;

import com.food.delivery.domain.UserModel;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link UserModel} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";
    Optional<UserModel> findOneByActivationKey(String activationKey);
    List<UserModel> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);
    Optional<UserModel> findOneByResetKey(String resetKey);
    Optional<UserModel> findOneByEmailIgnoreCase(String email);
    Optional<UserModel> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<UserModel> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<UserModel> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<UserModel> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
