package com.usth.chat_app_api.user_info;

import java.util.List;
import java.util.Optional;

public interface IUserInfoService {

    UserInfo findUserInforById(Long id);
    Optional<UserInfo> findByEmail(String email);
    UserInfo saveUserInfo(UserInfo userInfo);
    Optional<UserInfo> findByUsername(String username);
    Optional<UserInfo> findByEmailAndIsActive(String email, boolean isActive);
    Optional<UserInfo> findByUsernameAndIsActive(String username, boolean isActive);
    void deleteByEmail(String email);
    Optional<UserInfo> findByVerificationCode(String verificationCode);
    Optional<UserInfo> findByIdAndVerificationCode(Long id, String verificationCode);

    void deleteVerificationCode(String verificationCode);

    public List<UserInfo> searchUsers(Long currentUserId, String query, int page, int size);
}
