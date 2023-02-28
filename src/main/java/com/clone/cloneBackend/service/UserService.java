package com.clone.cloneBackend.service;


import com.clone.cloneBackend.configuration.jwt.JwtService;
import com.clone.cloneBackend.domain.AppUser;
import com.clone.cloneBackend.domain.PasswordResetToken;
import com.clone.cloneBackend.domain.Token;
import com.clone.cloneBackend.domain.TokenType;
import com.clone.cloneBackend.dto.AuthenticationDto;
import com.clone.cloneBackend.dto.EmailDetails;
import com.clone.cloneBackend.dto.RegistrationDto;
import com.clone.cloneBackend.dto.response.GenericResponse;
import com.clone.cloneBackend.repository.AppUserRepository;
import com.clone.cloneBackend.repository.PasswordResetTokenRepository;
import com.clone.cloneBackend.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> users = findByEmail(username);

        users.orElseThrow(() -> new UsernameNotFoundException("Could not find user"));

        return users.map(AppUser::new).get();
    }

    public Optional<AppUser> findByEmail(String username){
        return  userRepository.findByEmail(username);
    }
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final JavaMailSender javaMailSender;

    private final PasswordResetTokenRepository passwordTokenRepository;

    public RegistrationDto register(RegistrationDto registrationDto){
        log.info("API to register the User");

        AppUser userChecker = userRepository.findAppUsersByEmail(registrationDto.getEmail());

        if(userChecker==null){
            var user = AppUser.builder()
                    .firstName(registrationDto.getFirstName())
                    .lastName(registrationDto.getLastName())
                    .email(registrationDto.getEmail())
                    .gender(registrationDto.getGender())
                    .dateOfBirth(registrationDto.getDateOfBirth())
                    .password(passwordEncoder.encode(registrationDto.getPassword()))
                    .address(registrationDto.getAddress())
                    .phoneNumber(registrationDto.getPhoneNumber())
                    .role(registrationDto.getRole())
                    .build();
            userRepository.save(user);

            return  registrationDto;

        }
        else{
            return null;
        }

    }

    public GenericResponse authenticate(AuthenticationDto authenticationDto) {
        log.info("Authenticating User");
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        authenticationDto.getEmail(),authenticationDto.getPassword()
                ));
        var user =userRepository.findByEmail(authenticationDto.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return new GenericResponse("00",jwtToken,null,null);
    }

    private void saveUserToken(AppUser  user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(AppUser user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

//


//    public void resetPassword(HttpServletRequest request,String userEmail){
//        log.info("Reset Password API");
//        AppUser user = userRepository.findAppUsersByEmail(userEmail);
//        if(user == null){
//            System.out.println("User not found");
//        }
//        String token = UUID.randomUUID().toString();
//        System.out.println(token);
//        createPasswordResetTokenForUser(user, token);
//        javaMailSender.
//                send(constructResetTokenEmail(
//                        String.valueOf(request.getRequestURL()),
//                        request.getLocale(),
//                        token,user));
//
//
//    }
//
//    private void createPasswordResetTokenForUser(AppUser user, String token) {
//        PasswordResetToken myToken = new PasswordResetToken(token, user);
//        passwordTokenRepository.save(myToken);
//    }
//
//    private SimpleMailMessage constructResetTokenEmail(
//            String contextPath, Locale locale, String token, AppUser user) {
//        String url = contextPath + "/user/changePassword?token=" + token;
//        String message =  "Follow this link" + locale;
//
//        return constructEmail("Reset Password", message + " \r\n" + url, user);
//    }
//
//    private SimpleMailMessage constructEmail(String subject, String body,
//                                             AppUser user) {
//        SimpleMailMessage email = new SimpleMailMessage();
//        email.setSubject(subject);
//        email.setText(body);
//        email.setTo(user.getEmail());
//        email.setFrom("noReply@gmail.com");
//        return email;
//    }

    public int sendSimpleMail(EmailDetails details) {
        log.info("Calling the sendSimpleMail service");
        AppUser user = userRepository.findAppUsersByEmail(details.getRecipient());
        LocalDate dateCreated =  LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = dateCreated.format(dateTimeFormatter);

        if(user == null){
            return 0;
        }
        else{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            String link = "https://prizepick.onrender.com/api/v1/auth/resetpassword";
            String uid = UUID.randomUUID().toString();
            saveUUID(uid,user,date);
            simpleMailMessage.setFrom("noreply@prizepicks.com");
            simpleMailMessage.setSubject("Password Update");
            simpleMailMessage.setText("Click this link to reset password "+ link + uid);
            simpleMailMessage.setTo(details.getRecipient());

            javaMailSender.send(simpleMailMessage);
            return 1;

        }
    }

    public void saveUUID(String uid, AppUser user,String date){
//        PasswordResetToken checkToken = passwordTokenRepository.findPasswordResetTokenByToken(uid);
//        if(checkToken == null){
//
//        }
        PasswordResetToken token = new PasswordResetToken(uid,user,date);
        passwordTokenRepository.save(token);


    }
}
