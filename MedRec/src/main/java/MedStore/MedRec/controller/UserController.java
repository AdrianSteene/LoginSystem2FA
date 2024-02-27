package MedStore.MedRec.controller;

import MedStore.MedRec.dto.incoming.TwoFACode;
import MedStore.MedRec.dto.internal.LoginCredentials;
import MedStore.MedRec.dto.outgoing.JWT;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final AuthenticationService authenticationService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository, LoginRepository loginRepository) {
        this.authenticationService = new AuthenticationService(userRepository, loginRepository);
    }

    @GetMapping("/auth/login")
    LoginToken login(HttpServletRequest request) throws BadRequestException {
        log.info("Login request received, requestId: " + request.getRequestId());
        try {
            return authenticationService.login(request);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid login credentials");
        }
    }

    @GetMapping("/auth/twofa")
    JWT jwt(HttpServletRequest request, @RequestBody TwoFACode twoFACode) {
        log.info("2fa request received, requestId: " + request.getRequestId());
        //if (twoFACode == null) throw new BadRequestException("Invalid login credentials");
        return authenticationService.validate2FA(request, twoFACode);
    }
}
