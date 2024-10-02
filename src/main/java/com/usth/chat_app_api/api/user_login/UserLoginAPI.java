package com.usth.chat_app_api.api.user_login;

import com.usth.chat_app_api.core.base.ResponseMessage;
import com.usth.chat_app_api.jwt.JwtService;
import com.usth.chat_app_api.user_login.IUserLoginService;
import com.usth.chat_app_api.user_login.UserLogin;
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
@RequestMapping("/api/v1/user-login")
public class UserLoginAPI {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private IUserLoginService userLoginService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping(value = "/register")
    public ResponseEntity<UserLoginResponse> registerUser(@RequestBody UserLoginRequest request){
        UserLoginResponse response = new UserLoginResponse();
        final String emailSubject = "Complete Registration";
        final String randomToken = UUID.randomUUID().toString();
        try{
            UserLogin registerUser = request.getUserLogin();
            // checking account is already exist or not
            Optional<UserLogin> userLogin = userLoginService.findByEmailAndIsActive(registerUser.getEmail(), true);
            boolean isLoginNameExisted = userLoginService.findByLoginName(registerUser.getLoginName()).isPresent();
            //
            if(isLoginNameExisted){
                throw new Exception("Login name: "+ registerUser.getLoginName() +"already existed");
            } else if(userLogin.isEmpty()) {
                // delete un active account
                userLoginService.deleteByEmail(registerUser.getEmail());
                // hash and set new hash password
                String hashPwd = passwordEncoder.encode(registerUser.getPassword());
                registerUser.setPassword(hashPwd);
                registerUser.setConfirmationToken(randomToken);
                //save un-active registration user
                userLoginService.saveUserLogin(registerUser);
                // sending email
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//                simpleMailMessage.setFrom();
                simpleMailMessage.setTo(registerUser.getEmail());
                simpleMailMessage.setSubject(emailSubject);
                simpleMailMessage.setText("Your confirm code is:" + randomToken);
                javaMailSender.send(simpleMailMessage);
                // hash and set new hash password
                response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
                response.setStatusCode(HttpStatus.OK.value());
                ResponseEntity.status(HttpStatus.OK.value()).body(response);
            } else {
                throw new Exception("User: "+ registerUser.getEmail() +" already existed");
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                    .body(response);
        }
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<UserLoginResponse> confirmAccount(@RequestBody UserLoginRequest confirmationToken){
        UserLoginResponse response = new UserLoginResponse();
        try{
            String confirmationCode = confirmationToken.getConfirmationCode();
            Optional<UserLogin> registerUser = userLoginService.findByConfirmationToken(confirmationCode);
            //
            if(registerUser.isPresent()){
                // set active
                registerUser.get().setActive(true);
                // set create time
                registerUser.get().setCreatedAt(new Timestamp(System.currentTimeMillis()));
                userLoginService.saveUserLogin(registerUser.get());
                // delete confirmation code after authenticate
                userLoginService.deleteConfirmationCode(confirmationCode);
            }
            //
            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                    .body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> authenticate(@RequestBody UserLoginRequest request) {
        UserLoginResponse response = new UserLoginResponse();
        try {
            UserLogin userLogin = request.getUserLogin();
            String accountName;
            if(userLogin.getEmail() != null){
                accountName = userLogin.getEmail();
            } else if (userLogin.getLoginName() != null){
                accountName = userLogin.getLoginName();
            } else{
                throw new Exception("Cannot found login name or email!");
            }
            // authenticate account
            // if the account is not true, exception will be occurred
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            accountName,
                            userLogin.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtService.generateToken((UserDetails) authentication.getPrincipal());
            response.setJwt_token(jwtToken);
            System.out.println(jwtToken);

            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK.value()).body(response);
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                    .body(response);
        }
    }

    @PostMapping("/get-user-login")
    public ResponseEntity<UserLoginResponse> getUserLogin(@RequestBody UserLoginRequest request){
        UserLoginResponse response = new UserLoginResponse();
        try{
            UserLogin userLogin = request.getUserLogin();
            String loginName = userLogin.getLoginName();
            String email = userLogin.getEmail();
//            if(){
//
//            }
            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK.value()).body(response);
        } catch (Exception e){
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                    .body(response);
        }
    }

}
