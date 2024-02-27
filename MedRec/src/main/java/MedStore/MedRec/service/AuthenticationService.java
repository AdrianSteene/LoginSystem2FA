package MedStore.MedRec.service;

import MedStore.MedRec.crypt.MedRecCryptUtils;
import MedStore.MedRec.crypt.PasswordEncryptor;
import MedStore.MedRec.dto.internal.LoginCredentials;
import MedStore.MedRec.dto.outgoing.LoginToken;
import MedStore.MedRec.entities.Login;
import MedStore.MedRec.entities.User;
import MedStore.MedRec.repository.LoginRepository;
import MedStore.MedRec.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

public class AuthenticationService extends GenericService {
    private final LoginRepository loginRepository;
    private static final String AUTHORIZATION_HEADER = "Authorization";

    public AuthenticationService(UserRepository userRepository, LoginRepository loginRepository) {
        super(userRepository);
        this.loginRepository = loginRepository;
    }

    public LoginCredentials getBasicAuthLoginCredentials(HttpServletRequest request) throws BadRequestException {
        String username;
        String password;
        final String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            username = values[0];
            password = values[1];
            log.info("Basic Auth header present for request: " + request.getRequestId());
        } else {
            log.info("No basic auth header found for request: " + request.getRequestId());
            throw new BadRequestException("Invalid login credentials");
        }
        return new LoginCredentials(username, password);
    }

    public LoginToken createLoginToken(LoginCredentials loginCredentials, String salt, User user) throws BadRequestException {
        String passwordhash = PasswordEncryptor.encrypt(loginCredentials.password(), salt);
        if (PasswordEncryptor.isNotValidHash(user.getPasswordhash(), passwordhash)){
            log.info("Unsuccessful login attempt for user: " + user.getUserId());
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

