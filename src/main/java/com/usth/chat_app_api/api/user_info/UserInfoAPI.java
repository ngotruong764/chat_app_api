package com.usth.chat_app_api.api.user_info;

<<<<<<< HEAD
import com.usth.chat_app_api.config_websocket.WebSocketSessionManager;
=======
import com.usth.chat_app_api.constant.ApplicationConstant;
>>>>>>> f9516bf7e0063ca125202f84204fddf42feb1dd6
import com.usth.chat_app_api.core.base.ResponseMessage;
import com.usth.chat_app_api.jwt.JwtService;
import com.usth.chat_app_api.security_config.TalkieUserDetailService;
import com.usth.chat_app_api.user_info.IUserInfoService;
import com.usth.chat_app_api.user_info.UserInfo;
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
import org.springframework.web.socket.WebSocketSession;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

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
<<<<<<< HEAD
    @Autowired
    private WebSocketSessionManager sessionManager;
=======

>>>>>>> f9516bf7e0063ca125202f84204fddf42feb1dd6
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
<<<<<<< HEAD


=======
            // get user info
            UserInfo user = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            response.setUserInfo(user);
            //
>>>>>>> f9516bf7e0063ca125202f84204fddf42feb1dd6
            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            response.setResponseCode(HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK.value()).body(response);
        } catch (Exception e) {
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
            // get request
            UserInfo userInfo = request.getUserInfo();
            // update user info
            userInfoService.saveUserInfo(userInfo);
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
}
