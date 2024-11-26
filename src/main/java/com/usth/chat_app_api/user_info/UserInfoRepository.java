package com.usth.chat_app_api.user_info;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    UserInfo findUserInfoById(Long id);

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


    Optional<UserInfo> findByIdAndVerificationCode(Long id, String verificationCode);

    @Query(value = "select a from UserInfo a "
            + "where a.username = :username or a.email = :username "
            + "and a.isActive = :isActive")
    Optional<UserInfo> findByEmailOrUsernameAndIsActive(
            @Param("username") String username,
            @Param("isActive") boolean isActive);
    @Query("SELECT DISTINCT u FROM UserInfo u " +
            "JOIN MessageRecipient mr ON mr.recipient.id = u.id " +
            "WHERE mr.message.creatorId.id = :userId OR mr.recipient.id = :userId")
    List<UserInfo> findMessageUsers(@Param("userId") Long userId);
    @Query("""
    SELECT DISTINCT u FROM UserInfo u
    JOIN u.conversations c
    WHERE c.id IN (
        SELECT c2.id FROM Conversation c2
        JOIN c2.participants p
        WHERE p.id = :userId
    )
""")
    List<UserInfo> findConversationUsers(@Param("userId") Long userId);

    @Query("""
    SELECT u FROM UserInfo u
    WHERE u.id NOT IN (
        SELECT DISTINCT u1.id FROM UserInfo u1
        JOIN MessageRecipient mr ON mr.recipient.id = u1.id
        WHERE mr.message.creatorId.id = :userId OR mr.recipient.id = :userId
    )
    AND u.id NOT IN (
        SELECT DISTINCT u2.id FROM UserInfo u2
        JOIN u2.conversations c
        WHERE c.id IN (
            SELECT c3.id FROM Conversation c3
            JOIN c3.participants p
            WHERE p.id = :userId
        )
    )
""")
    List<UserInfo> findStrangers(@Param("userId") Long userId);


}
