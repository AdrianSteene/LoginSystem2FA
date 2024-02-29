package MedStore.MedRec.controller;

import MedStore.MedRec.dto.incoming.TwoFACode;
import MedStore.MedRec.dto.internal.UserDto;
import MedStore.MedRec.dto.outgoing.JWT;
import MedStore.MedRec.dto.outgoing.LoginToken;
import MedStore.MedRec.service.AuthenticationService;
import MedStore.MedRec.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/auth/login")
    LoginToken login(HttpServletRequest request) throws BadRequestException {
        log.info("Login request received, requestId: " + request.getRequestId());
        try {
            return authenticationService.login(request);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid login credentials");
        }
    }

    @GetMapping("/auth/2fa")
    JWT jwt(HttpServletRequest request, @RequestBody TwoFACode twoFACode) throws BadRequestException {
        log.info("2fa request received, requestId: " + request.getRequestId());
        if (twoFACode == null || twoFACode.twoFACode().isBlank())
            throw new BadRequestException("Invalid login credentials");
        return authenticationService.validate2FALogin(request, twoFACode);
    }

    // THIS IS AN ENDPOINT FOR TESTING
    @GetMapping("/auth/testJWT")
    String testJWT(HttpServletRequest request) {
        log.info("Test JWT request received, requestId: " + request.getRequestId());
        UserDto userDto = authenticationService.validateJWT(request);
        return "nice";
    }
}
