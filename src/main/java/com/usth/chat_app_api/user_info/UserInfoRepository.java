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

    @Query("SELECT u FROM UserInfo u WHERE UPPER(u.username) LIKE UPPER(CONCAT('%', :username, '%')) OR UPPER(u.email) LIKE UPPER(CONCAT('%', :email, '%'))")
    List<UserInfo> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(@Param("username") String username, @Param("email") String email);


    @Query("SELECT DISTINCT u FROM ConversationParticipant cp JOIN cp.user u WHERE cp.conversation.id IN :conversationIds")
    List<UserInfo> findUsersByConversations(List<Long> conversationIds);


}
