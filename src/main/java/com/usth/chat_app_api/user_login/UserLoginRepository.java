package com.usth.chat_app_api.user_login;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {
    Optional<UserLogin> findByEmail(String email);
    Optional<UserLogin> findByEmailAndIsActive(String email, boolean isActive);

    void deleteByEmail(String email);

    Optional<UserLogin> findByConfirmationToken(String confirmationToken);

    Optional<UserLogin> findByLoginName(String loginName);

    Optional<UserLogin> findByLoginNameAndIsActive(String login, boolean isActive);

    @Query(value = "update UserLogin a "
            + " set a.confirmationToken = null"
            + " where a.confirmationToken = :confirmationCode")
    void deleteConfirmationCode(@Param("confirmationCode") String confirmationCode);
}
