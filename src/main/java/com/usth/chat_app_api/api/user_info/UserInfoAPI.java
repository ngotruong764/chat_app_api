package com.usth.chat_app_api.api.user_info;

import com.usth.chat_app_api.aws.IAwsS3Service;
import com.usth.chat_app_api.constant.ApplicationConstant;
import com.usth.chat_app_api.core.base.ResponseMessage;
import com.usth.chat_app_api.jwt.JwtService;
import com.usth.chat_app_api.user_info.IUserInfoService;
import com.usth.chat_app_api.user_info.UserInfo;
import com.usth.chat_app_api.utils.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user-info")
@Slf4j
public class UserInfoAPI {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IAwsS3Service awsS3Service;

    @GetMapping("/test")
    public String testDeploy(){
        return "It works";
    }

    @PostMapping(value = "/register")
    public ResponseEntity<UserInfoResponse> registerUser(@RequestBody UserInfoRequest request){
        UserInfoResponse response = new UserInfoResponse();
        final String emailSubject = "Complete Registration";
        // create random token
        final int MIN =ApplicationConstant.MIN;
        final int MAX =ApplicationConstant.MAX;
        String randomToken = String.valueOf((int)(Math.random()*(MAX-MIN+1)+MIN));
        try{
            UserInfo registerUser = request.getUserInfo();
            // checking account is already exist or not
            Optional<UserInfo> userInfo = userInfoService.findByEmailAndIsActive(registerUser.getEmail(), true);
            boolean isUsername = userInfoService.findByUsername(registerUser.getUsername()).isPresent();
            //
            if(isUsername){
                throw new Exception("Username: "+ registerUser.getUsername() +"already existed!");
            } else if(userInfo.isEmpty()) {
                // delete un active account
                userInfoService.deleteByEmail(registerUser.getEmail());
                // hash and set new hash password
                String hashPwd = passwordEncoder.encode(registerUser.getPassword());
                registerUser.setPassword(hashPwd);
                registerUser.setVerificationCode(randomToken);
                //save un-active registration user
                UserInfo savedUser = userInfoService.saveUserInfo(registerUser);
                // sending email
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//                simpleMailMessage.setFrom();
                simpleMailMessage.setTo(registerUser.getEmail());
                simpleMailMessage.setSubject(emailSubject);
                simpleMailMessage.setText("Your verification code is: " + randomToken);
                javaMailSender.send(simpleMailMessage);
                //
                response.setUserInfo(savedUser);
                response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
                response.setResponseCode(HttpStatus.OK.value());
                ResponseEntity.status(HttpStatus.OK.value()).body(response);
            } else {
                throw new Exception("User: "+ registerUser.getEmail() +" already existed");
            }
            //
            response.setResponseCode(HttpStatus.OK.value());
            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                    .body(response);
        }
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<UserInfoResponse> confirmAccount(@RequestBody UserInfoRequest request){
        UserInfoResponse response = new UserInfoResponse();
        try{
            UserInfo user = request.getUserInfo();
            // Get requests
            Long userId = user.getId();
            String verificationCode = user.getVerificationCode();
            Optional<UserInfo> registerUser = userInfoService.findByIdAndVerificationCode(userId, verificationCode);
            //
            if(registerUser.isPresent()){
                // set active
                registerUser.get().setActive(true);
                // set create time
                registerUser.get().setCreateAt(new Timestamp(System.currentTimeMillis()));
                registerUser.get().setUpdateAt(new Timestamp(System.currentTimeMillis()));
                registerUser.get().setVerificationCode(null);   // delete verification code
                UserInfo userInfo = userInfoService.saveUserInfo(registerUser.get());
                // set user info response
                response.setUserInfo(userInfo);
            }
            //
            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            response.setResponseCode(HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                    .body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserInfoResponse> authenticate(@RequestBody UserInfoRequest request) throws Exception {
        UserInfoResponse response = new UserInfoResponse();
        try {
            UserInfo userInfo = request.getUserInfo();
            String accountName;
            if(userInfo.getEmail() != null){
                accountName = userInfo.getEmail();
            } else if (userInfo.getUsername() != null){
                accountName = userInfo.getUsername();
            } else{
                throw new Exception("Cannot found login name or email!");
            }
            // authenticate account
            // if the account is not true, exception will be occurred
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            accountName,
                            userInfo.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtService.generateToken((UserDetails) authentication.getPrincipal());
            response.setJwt_token(jwtToken);
            System.out.println(jwtToken);
            // get user info
            UserInfo user = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // if the authentication is true, we save the device_token
            if(user.getId() != null){
                // set isOnline
                user.setStatus(true);

                // device token
                if(!userInfo.getDeviceToken().equals(user.getDeviceToken())){
                    user.setDeviceToken(userInfo.getDeviceToken());
                }
                userInfoService.saveUserInfo(user);

                // get user avatar
                byte[] userAvatar = awsS3Service.downLoadObject(ApplicationConstant.AWS_BUCKET_NAME, user.getProfilePicture());
                user.setProfilePicture(null);
                if(userAvatar.length > 0 && Helper.isValidImg(userAvatar)){
                    // convert byte[] to base64
                    String avatarBase64Encoded = Base64.getEncoder().encodeToString(userAvatar);
                    user.setProfilePicture(avatarBase64Encoded);
                }
                response.setUserInfo(user);
            }
            //
            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            response.setResponseCode(HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK.value()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                    .body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<UserInfoResponse> logout(){
        UserInfoResponse response = new UserInfoResponse();
        try{
            UserInfo user = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // delete device token
            user.setDeviceToken(null);
            // set status to false --> offline
            user.setStatus(false);

            userInfoService.saveUserInfo(user);

            // clear context
            SecurityContextHolder.clearContext();
            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            response.setResponseCode(HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK.value()).body(response);
        } catch (Exception e){
            log.info(e.getMessage());
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                    .body(response);
        }
    }

    @PostMapping("/get-user-login")
    public ResponseEntity<UserInfoResponse> getUserLogin(@RequestBody UserInfoRequest request){
        UserInfoResponse response = new UserInfoResponse();
        try{
            UserInfo userLogin = request.getUserInfo();
            String username = userLogin.getUsername();
            String email = userLogin.getEmail();
//            if(){
//
//            }
            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            response.setResponseCode(HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK.value()).body(response);
        } catch (Exception e){
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                    .body(response);
        }
    }

    // Save use info
    @PostMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestBody UserInfoRequest request){
        UserInfoResponse response = new UserInfoResponse();
        try{
            UserInfo updatedUser;
            // get request
            UserInfo userInfo = request.getUserInfo();
            // if is a new user
            if(userInfo.getId() == null){
                // update user info
                updatedUser = userInfoService.saveUserInfo(userInfo);
                response.setUserInfo(updatedUser);
            } else{
                // get current user
//                UserInfo user = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                UserInfo user = userInfoService.findUserInforById(userInfo.getId());

                if(user != null){
                    user.setFirstName(userInfo.getFirstName());
                    user.setLastName(userInfo.getLastName());
                    user.setUsername(userInfo.getUsername());
//                user.setEmail(userInfo.getEmail());
                    user.setPhoneNumber(userInfo.getPhoneNumber());
                    user.setSex(userInfo.getSex());
                    user.setUpdateAt(new Timestamp(System.currentTimeMillis()));

                    if(userInfo.getProfilePicture() != null &&
                            !userInfo.getProfilePicture().isEmpty() &&
                            ApplicationConstant.AWS_BUCKET_NAME != null){
                        // create key name for obj
                        String keyName = "img/"+ user.getId() + "/" + userInfo.getProfilePicture();
                        // upload img to aws bucket
                        boolean isUpdated = awsS3Service.uploadObject(ApplicationConstant.AWS_BUCKET_NAME, keyName, 0L, "", userInfo.getProfilePictureBase64());
                        if(!isUpdated){
                            throw new Exception("Cannot update user profile picture");
                        }
                        user.setProfilePicture(keyName);
                    }

                    updatedUser = userInfoService.saveUserInfo(user);
                    response.setUserInfo(updatedUser);
                }
            }
            //
            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            response.setResponseCode(HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK.value()).body(response);
        } catch (Exception e){
            log.info(e.getMessage());
            response.setMessage(e.getMessage());
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
        }
    }
    @GetMapping("/searchUsersByUsername")
    public ResponseEntity<?> searchUsers(
            @RequestParam String username,
            @RequestParam Long currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<UserInfo> users = userInfoService.searchUsers(currentUserId, username, page, size);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while searching for users: " + e.getMessage());
        }
    }

    @PostMapping("/pushVerificationCode")
    public ResponseEntity<?> pushVerificationCode(@RequestBody UserInfoRequest request){
        UserInfoResponse response = new UserInfoResponse();
        final String emailSubject = "Talkie - Forget Password";
        try{
            // get params
            String userEmail = request.getUserEmail();

            // find user by email
            Optional<UserInfo> userInfo = userInfoService.findByEmail(userEmail);
            if(userInfo.isEmpty()){
                throw new Exception("User not found");
            } else{
                // get random token
                final int MIN =ApplicationConstant.MIN;
                final int MAX =ApplicationConstant.MAX;
                String randomToken = String.valueOf((int)(Math.random()*(MAX-MIN+1)+MIN));

                // Send email
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//                simpleMailMessage.setFrom();
                simpleMailMessage.setTo(userInfo.get().getEmail());
                simpleMailMessage.setSubject(emailSubject);
                simpleMailMessage.setText("Your verification code is: " + randomToken);
                javaMailSender.send(simpleMailMessage);

                // save confirmation code
                userInfo.get().setVerificationCode(randomToken);
                userInfoService.saveUserInfo(userInfo.get());

                response.setUserInfo(userInfo.get());
            }

            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            response.setResponseCode(HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK.value()).body(response);
        } catch (Exception e){
            log.info(e.getMessage());
            response.setMessage(e.getMessage());
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
        }
    }

}
