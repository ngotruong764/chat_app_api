package com.usth.chat_app_api.user_info;
import java.util.Optional;

public interface IUserInfoService {
//<<<<<<< HEAD
    UserInfo findUserInforById(Long id);
//=======
    Optional<UserInfo> findByEmail(String email);
    UserInfo saveUserInfo(UserInfo userInfo);
    Optional<UserInfo> findByUsername(String username);
    Optional<UserInfo> findByEmailAndIsActive(String email, boolean isActive);
    Optional<UserInfo> findByUsernameAndIsActive(String username, boolean isActive);
    void deleteByEmail(String email);
    Optional<UserInfo> findByVerificationCode(String verificationCode);

    void deleteVerificationCode(String verificationCode);
//>>>>>>> 5afd2c5b5caabba5ce3c3efdcbfde67740e84afc
}
