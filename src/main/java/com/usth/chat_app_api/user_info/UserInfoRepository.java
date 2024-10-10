package com.usth.chat_app_api.user_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
//<<<<<<< HEAD
    UserInfo findUserInfoById(Long id);
//=======
    Optional<UserInfo> findByEmail(String email);
    Optional<UserInfo> findByEmailAndIsActive(String email, boolean isActive);

    void deleteByEmail(String email);
    Optional<UserInfo> findByVerificationCode(String verificationCode);

    Optional<UserInfo> findByUsername(String loginName);

    Optional<UserInfo> findByUsernameAndIsActive(String login, boolean isActive);

    @Query(value = "update UserInfo a "
            + " set a.verificationCode = null"
            + " where a.verificationCode = :verificationCode")
    void deleteVerificationCode(@Param("verificationCode") String confirmationCode);
//>>>>>>> 5afd2c5b5caabba5ce3c3efdcbfde67740e84afc
}
