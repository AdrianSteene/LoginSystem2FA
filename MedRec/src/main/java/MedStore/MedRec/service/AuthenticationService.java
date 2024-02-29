package MedStore.MedRec.service;

import MedStore.MedRec.crypt.MedRecCryptUtils;
import MedStore.MedRec.crypt.PasswordEncryptor;
import MedStore.MedRec.dto.incoming.TwoFACode;
import MedStore.MedRec.dto.internal.LoginCredentials;
import MedStore.MedRec.dto.internal.UserDto;
import MedStore.MedRec.dto.outgoing.JWT;
import MedStore.MedRec.dto.outgoing.LoginToken;
import MedStore.MedRec.entities.Login;
import MedStore.MedRec.entities.TwoFA;
import MedStore.MedRec.entities.User;
import MedStore.MedRec.enums.Role;
import MedStore.MedRec.repository.LoginRepository;
import MedStore.MedRec.repository.TwoFARepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService extends GenericService {

    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private TwoFARepository twoFARepository;
    @Autowired
    private EmailService emailService;

    private final String AUTHORIZATION_HEADER = "Authorization";
    private final long JWT_EXPIRATION_TIME = 1/* days */ * 1/* hours */ * 15/* minutes */;
    private final long TWOFA_EXPIRATION_TIME = 30/* days */ * 24/* hours */ * 60/* minutes */;
    private final String jwtSecret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4"; // TODO: fetch from vault
    private final String userId = "userId";
    private final String role = "role";
    private final String divisionId = "divisionId";

    public LoginToken login(HttpServletRequest request) throws BadRequestException {
        LoginCredentials loginCredentials = getBasicAuthLoginCredentials(request);
        User user = Optional
                .ofNullable(getUser(loginCredentials.username()))
                .orElseThrow(() -> {
                    log.info("Unsuccessful login attempt, requestId: " + request.getRequestId());
                    return new IllegalArgumentException("Invalid login credentials");
                });
        String salt = user.getSalt();

        return createLoginToken(loginCredentials, salt, user);
    }

    private LoginCredentials getBasicAuthLoginCredentials(HttpServletRequest request) throws BadRequestException {
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

    private LoginToken createLoginToken(LoginCredentials loginCredentials, String salt, User user)
            throws BadRequestException {
        String passwordhash = PasswordEncryptor.encrypt(loginCredentials.password(), salt);
        if (PasswordEncryptor.isNotValidHash(user.getPasswordhash(), passwordhash)) {
            log.info("Unsuccessful login attempt for user: " + user.getUserId());
            throw new BadRequestException("Invalid login credentials");
        } else {
            Login login = new Login();
            login.setUserId(user.getUserId());
            String loginToken = MedRecCryptUtils.randomString(MedRecCryptUtils.TOKEN_LENGTH);
            login.setLoginToken(loginToken);
            login.setCreated(Instant.now());
            login.setExpired(false);
            log.info("Created Login Token " + loginRepository.save(login));

            TwoFA twoFA = new TwoFA();
            twoFA.setUserId(user.getUserId());
            twoFA.setTwoFACode(MedRecCryptUtils.random2FAString());
            twoFA.setCreated(Instant.now());
            twoFA.setExpired(false);
            log.info("Created 2FA code " + twoFARepository.save(twoFA));

            emailService.sendSimpleMessage(user.getUsername(), "2FA code", twoFA.getTwoFaCode());

            return new LoginToken(loginToken);
        }
    }

    public JWT validate2FALogin(HttpServletRequest request, TwoFACode twoFACode) throws IllegalArgumentException {
        String loginToken = extractBearerToken(request);
        User user = validateLoginToken(loginToken);
        validate2FACode(twoFACode, user.getUserId());
        return createJWT(user);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader == null || authorizationHeader.isBlank())
            throw new IllegalArgumentException("Invalid login credentials");
        return authorizationHeader.substring("Bearer".length()).trim().replaceAll("\"", "");
    }

    private User validateLoginToken(String loginToken) {
        Login login = loginRepository.findByLoginToken(loginToken);
        if (login == null || login.isExpired() || isNotWithinUsageTime(login))
            throw new IllegalArgumentException("Invalid login credentials");
        login.setExpired(true);
        loginRepository.save(login);
        return getUser(login.getUserId());
    }

    private boolean isNotWithinUsageTime(Login login) {
        return login.getCreated().isBefore(Instant.now().minus(TWOFA_EXPIRATION_TIME, ChronoUnit.MINUTES));
    }

    private boolean isNotWithinUsageTime(TwoFA twoFA) {
        return twoFA.getCreated().isBefore(Instant.now().minus(TWOFA_EXPIRATION_TIME, ChronoUnit.MINUTES));
    }

    private void validate2FACode(TwoFACode twoFACode, long userId) {
        TwoFA twoFA = twoFARepository.findByUserIdAndTwoFACode(userId, twoFACode.twoFACode());
        if (twoFA == null || twoFA.isExpired() || isNotWithinUsageTime(twoFA)) {
            throw new IllegalArgumentException("Invalid login credentials");
        }
        twoFA.setExpired(true);
        twoFARepository.save(twoFA);
    }

    private JWT createJWT(User user) {
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(jwtSecret),
                SignatureAlgorithm.HS256.getJcaName());
        Instant now = Instant.now();
        String token = Jwts.builder()
                .claim(userId, user.getUserId())
                .claim(role, user.getRole().toString())
                .claim(divisionId, user.getDivisionId())
                .setSubject(user.getUsername())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(JWT_EXPIRATION_TIME, ChronoUnit.MINUTES)))
                .signWith(hmacKey)
                .compact();

        return new JWT(token);
    }

    public UserDto validateJWT(HttpServletRequest request) {
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(jwtSecret),
                SignatureAlgorithm.HS256.getJcaName());
        String jwtToken = extractBearerToken(request);
        try {
            Jws<Claims> jwtClaims = Jwts.parserBuilder()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(jwtToken);
            Claims body = jwtClaims.getBody();
            long claimedUserID = (long) ((int) Optional.ofNullable(body.get(userId))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid token")));
            Role claimedRole = Role.valueOf(Optional.ofNullable(body.get(role))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid token")).toString());
            Object o = body.get(divisionId);
            Long claimedDivisionId = o == null ? null : (Long) ((long) ((int) o));
            return new UserDto(claimedUserID, claimedRole, claimedDivisionId);
        } catch (ExpiredJwtException e) {
            log.info(String.format("Token %s expired", jwtToken));
            throw new IllegalArgumentException("Token expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info(String.format("Token %s unsupported", jwtToken));
            throw new IllegalArgumentException("Token unsupported: " + e.getMessage());
        } catch (MalformedJwtException e) {
            log.info(String.format("Token %s malformed", jwtToken));
            throw new IllegalArgumentException("Token malformed: " + e.getMessage());
        } catch (SignatureException e) {
            log.info(String.format("Token %s signature exception", jwtToken));
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
