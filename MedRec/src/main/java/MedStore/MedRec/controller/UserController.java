package MedStore.MedRec.controller;

import MedStore.MedRec.crypt.MedRecCryptUtils;
import MedStore.MedRec.crypt.PasswordEncryptor;
import MedStore.MedRec.dto.outgoingDto.LoginToken;
import MedStore.MedRec.entities.Login;
import MedStore.MedRec.entities.User;
import MedStore.MedRec.repository.LoginRepository;
import MedStore.MedRec.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;
    private final LoginRepository loginRepository;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    public UserController(UserRepository userRepository, LoginRepository loginRepository) {
        this.userRepository = userRepository;
        this.loginRepository = loginRepository;
    }

    @GetMapping("/users")
    List<User> all() {
        return userRepository.findAll();
    }

    @GetMapping("/login")
    LoginToken login(HttpServletRequest request) throws BadRequestException {
        String username;
        String password;
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            username = values[0];
            password = values[1];
        } else {
            throw new BadRequestException("Invalid login credentials");
        }

        User user =
                Optional
                        .ofNullable(userRepository.findByUsername(username))
                        .orElseThrow(() -> new BadRequestException("Invalid login credentials"));
        String salt = user.getSalt();

        String passwordhash = PasswordEncryptor.encrypt(password, salt);
        if (!PasswordEncryptor.checkPassword(user.getPasswordhash(), passwordhash)){
            throw new BadRequestException("Invalid login credentials");
        } else {
            Login login = new Login();
            login.setUserId(user.getUserId());
            String loginToken = MedRecCryptUtils.randomString(MedRecCryptUtils.TOKEN_LENGTH);
            login.setLoginToken(loginToken);
            login.setCreated(LocalDateTime.now());
            login.setExpired(false);
            log.info("Created Login Token " + loginRepository.save(login));
            return new LoginToken(loginToken);
        }
    }
}
