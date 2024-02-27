package MedStore.MedRec.controller;

import MedStore.MedRec.dto.internal.LoginCredentials;
import MedStore.MedRec.dto.outgoing.LoginToken;
import MedStore.MedRec.entities.User;
import MedStore.MedRec.repository.LoginRepository;
import MedStore.MedRec.repository.UserRepository;
import MedStore.MedRec.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController {
    private final AuthenticationService authenticationService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository, LoginRepository loginRepository) {
        this.authenticationService = new AuthenticationService(userRepository, loginRepository);
    }

    @GetMapping("/login")
    LoginToken login(HttpServletRequest request) throws BadRequestException {
        log.info("Login request received, requestId: " + request.getRequestId());
        LoginCredentials loginCredentials = authenticationService.getBasicAuthLoginCredentials(request);
        User user =
                Optional
                        .ofNullable(authenticationService.getUser(loginCredentials.username()))
                        .orElseThrow(() -> {
                            log.info("Unsuccessful login attempt, requestId: " + request.getRequestId());
                            return new BadRequestException("Invalid login credentials");
                        });
        String salt = user.getSalt();

        return authenticationService.createLoginToken(loginCredentials, salt, user);
    }
}
