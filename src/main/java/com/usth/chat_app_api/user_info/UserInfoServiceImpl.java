package com.usth.chat_app_api.user_info;

import com.usth.chat_app_api.aws.IAwsS3Service;
import com.usth.chat_app_api.constant.ApplicationConstant;
import com.usth.chat_app_api.conversation.ConversationRepository;
import com.usth.chat_app_api.utils.Helper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserInfoServiceImpl implements IUserInfoService {
    @Autowired
    private UserInfoRepository repo;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private IAwsS3Service awsS3Service;

    @Override
    public UserInfo findUserInforById(Long id) {
        return repo.findUserInfoById(id);
    }

    @Override
    public Optional<UserInfo> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public UserInfo saveUserInfo(UserInfo userInfo) {
        return repo.save(userInfo);
    }


    @Override
    public Optional<UserInfo> findByUsername(String loginName) {
        return repo.findByUsername(loginName);
    }

    @Override
    public Optional<UserInfo> findByEmailAndIsActive(String email, boolean isActive) {
        return repo.findByEmailAndIsActive(email, isActive);
    }

    @Override
    public Optional<UserInfo> findByUsernameAndIsActive(String username, boolean isActive) {
        return Optional.empty();
    }


    @Override
    public void deleteByEmail(String email) {
        repo.deleteByEmail(email);
    }

    @Override
    public Optional<UserInfo> findByVerificationCode(String verificationCode) {
        return repo.findByVerificationCode(verificationCode);
    }

    @Override
    public Optional<UserInfo> findByIdAndVerificationCode(Long id, String verificationCode) {
        return repo.findByIdAndVerificationCode(id, verificationCode);
    }

    @Override
    public void deleteVerificationCode(String verificationCode) {
        repo.deleteVerificationCode(verificationCode);
    }

    @Override
    public List<UserInfo> searchUsers(Long currentUserId, String query, int page, int size) {
        List<Long> conversationIds = conversationRepository.findConversationIdsByUserId(currentUserId);
        List<UserInfo> allUsers = repo.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
        List<UserInfo> chattedUsers = new ArrayList<>();
        List<UserInfo> sameConversationUsers = new ArrayList<>();
        List<UserInfo> strangerUsers = new ArrayList<>();

        for (UserInfo user : allUsers) {
            if (!user.getId().equals(currentUserId)) {
                if (conversationIds.contains(user.getId())) {
                    sameConversationUsers.add(user);
                } else if (repo.findUsersByConversations(conversationIds).contains(user)) {
                    chattedUsers.add(user);
                } else {
                    strangerUsers.add(user);
                }

                // get user avatar
                if(user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()){
                    byte[] userAvatar = awsS3Service.downLoadObject(ApplicationConstant.AWS_BUCKET_NAME, user.getProfilePicture());
                    if(userAvatar.length > 0 && Helper.isValidImg(userAvatar)){
                        // convert byte[] to base64
                        String avatarBase64Encoded = Base64.getEncoder().encodeToString(userAvatar);
                        user.setProfilePictureBase64(avatarBase64Encoded);
                    }
                }
            }
        }

        List<UserInfo> sortedUsers = new ArrayList<>();
        sortedUsers.addAll(chattedUsers);
        sortedUsers.addAll(sameConversationUsers);
        sortedUsers.addAll(strangerUsers);

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, sortedUsers.size());
        if (startIndex > sortedUsers.size()) {
            return new ArrayList<>();
        }

        return sortedUsers.subList(startIndex, endIndex);
    }

}
