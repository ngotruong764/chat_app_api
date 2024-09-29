package com.usth.chat_app_api.api.user_login;

import com.usth.chat_app_api.core.base.ResponseMessage;
import com.usth.chat_app_api.jwt.JwtService;
import com.usth.chat_app_api.user_login.UserLogin;
import com.usth.chat_app_api.user_login.UserLoginRepository;
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
    private UserLoginRepository userLoginRepo;
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
            Optional<UserLogin> userLogin = userLoginRepo.findByEmailAndIsActive(registerUser.getEmail(), true);
            if(userLogin.isEmpty()){
                // delete un active account
                userLoginRepo.deleteByEmail(registerUser.getEmail());
                // hash and set new hash password
                String hashPwd = passwordEncoder.encode(registerUser.getPassword());
                registerUser.setPassword(hashPwd);
                registerUser.setConfirmationToken(randomToken);
                //save un-active registration user
                userLoginRepo.save(registerUser);
                // sending email
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//                simpleMailMessage.setFrom();
                simpleMailMessage.setTo(registerUser.getEmail());
                simpleMailMessage.setSubject(emailSubject);
                simpleMailMessage.setText("To confirm your account, please click here : "
                        +"http://localhost:8081/talkie/api/v1/user-login/confirm-account?token="+randomToken);
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
    public ResponseEntity<UserLoginResponse> confirmAccount(@RequestParam("token")String confirmationToken){
        UserLoginResponse response = new UserLoginResponse();
        try{
            Optional<UserLogin> registerUser = userLoginRepo.findByConfirmationToken(confirmationToken);
            //
            if(registerUser.isPresent()){
                // set active
                registerUser.get().setActive(true);
                // set create time
                registerUser.get().setCreatedAt(new Timestamp(System.currentTimeMillis()));
                userLoginRepo.save(registerUser.get());
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

            // authenticate account
            // if the account is not true, exception will be occurred
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLogin.getEmail(),
                            userLogin.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtService.generateToken((UserDetails) authentication.getPrincipal());
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
}
