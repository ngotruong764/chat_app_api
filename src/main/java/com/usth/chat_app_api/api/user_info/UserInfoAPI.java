package com.usth.chat_app_api.api.user_info;

import com.usth.chat_app_api.core.base.ResponseMessage;
import com.usth.chat_app_api.jwt.JwtService;
import com.usth.chat_app_api.user_info.IUserInfoService;
import com.usth.chat_app_api.user_info.UserInfo;
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
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-info")
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
    @PostMapping(value = "/register")
    public ResponseEntity<UserInfoResponse> registerUser(@RequestBody UserInfoRequest request){
        UserInfoResponse response = new UserInfoResponse();
        final String emailSubject = "Complete Registration";
        final String randomToken = UUID.randomUUID().toString();
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
                userInfoService.saveUserInfo(registerUser);
                // sending email
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//                simpleMailMessage.setFrom();
                simpleMailMessage.setTo(registerUser.getEmail());
                simpleMailMessage.setSubject(emailSubject);
                simpleMailMessage.setText("Your verification code is:" + randomToken);
                javaMailSender.send(simpleMailMessage);
                // hash and set new hash password
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
            String verificationCode = request.getVerificationCode();
            Optional<UserInfo> registerUser = userInfoService.findByVerificationCode(verificationCode);
            //
            if(registerUser.isPresent()){
                // set active
                registerUser.get().setActive(true);
                // set create time
                registerUser.get().setCreateAt(new Timestamp(System.currentTimeMillis()));
                registerUser.get().setUpdateAt(new Timestamp(System.currentTimeMillis()));
                registerUser.get().setVerificationCode(null);   // delete verification code
                userInfoService.saveUserInfo(registerUser.get());
            }
            //
            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                    .body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserInfoResponse> authenticate(@RequestBody UserInfoRequest request) {
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

    @PostMapping("/hello")
    public String helloworld(){
        return "Hello";
    }
}
