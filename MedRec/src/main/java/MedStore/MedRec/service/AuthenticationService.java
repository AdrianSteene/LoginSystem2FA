package MedStore.MedRec.service;

import MedStore.MedRec.crypt.MedRecCryptUtils;
import MedStore.MedRec.crypt.PasswordEncryptor;
import MedStore.MedRec.dto.incoming.IncomingJWT;
import MedStore.MedRec.dto.incoming.TwoFACode;
import MedStore.MedRec.dto.internal.LoginCredentials;
import MedStore.MedRec.dto.internal.UserDto;
import MedStore.MedRec.dto.outgoing.JWT;
import MedStore.MedRec.dto.outgoing.LoginToken;
import MedStore.MedRec.entities.Login;
import MedStore.MedRec.entities.User;
import MedStore.MedRec.enums.Role;
import MedStore.MedRec.repository.LoginRepository;
import MedStore.MedRec.repository.UserRepository;
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

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class AuthenticationService extends GenericService {
    private final LoginRepository loginRepository;
    private final String AUTHORIZATION_HEADER = "Authorization";
    private final long EXPIRATION_TIME = 30/*days*/ * 24/*hours*/ * 60/*minutes*/;
    private final String jwtSecret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4"; //TODO: fetch from vault
    private final String userId = "userId";
    private final String role = "role";
    private final String divisionId = "divisionId";

    public AuthenticationService(UserRepository userRepository, LoginRepository loginRepository) {
        super(userRepository);
        this.loginRepository = loginRepository;
    }

    public LoginToken login(HttpServletRequest request) throws BadRequestException {
        LoginCredentials loginCredentials = getBasicAuthLoginCredentials(request);
        User user =
                Optional
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

    private LoginToken createLoginToken(LoginCredentials loginCredentials, String salt, User user) throws BadRequestException {
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
            return new LoginToken(loginToken);
        }
    }

    public JWT validate2FA(HttpServletRequest request, TwoFACode twoFACode) {
        final String authorization = request.getHeader(AUTHORIZATION_HEADER);
        log.error("Authorization: " + authorization);
        return new JWT("thisisatoken");
    }

    private JWT createJWT(User user) {
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(jwtSecret),
                SignatureAlgorithm.HS256.getJcaName());
        Instant now = Instant.now();
        String token =
                Jwts.builder()
                        .claim(userId, user.getUserId())
                        .claim(role, user.getRole().toString())
                        .claim(divisionId, user.getDivisionId())
                        .setSubject(user.getUsername())
                        .setId(UUID.randomUUID().toString())
                        .setIssuedAt(Date.from(now))
                        .setExpiration(Date.from(now.plus(EXPIRATION_TIME, ChronoUnit.MINUTES)))
                        .signWith(hmacKey)
                        .compact();

        return new JWT(token);
    }

    public UserDto validateToken(IncomingJWT jwt) {
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(jwtSecret),
                SignatureAlgorithm.HS256.getJcaName());
        try {
            Jws<Claims> jwtClaims = Jwts.parserBuilder()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(jwt.getToken());
            Claims body = jwtClaims.getBody();
            long claimedUserID =
                    (Long) Optional.ofNullable(body.get(userId)).orElseThrow(() -> new IllegalArgumentException("Invalid token"));
            Role claimedRole =
                    Role.valueOf(Optional.ofNullable(body.get(role)).orElseThrow(() -> new IllegalArgumentException("Invalid token")).toString());
            Long claimedDivisionId = (Long) body.get(divisionId);
            return new UserDto(claimedUserID, claimedRole, claimedDivisionId);
        } catch (ExpiredJwtException e) {
            log.info(String.format("Token %s expired", jwt.getToken()));
            throw new IllegalArgumentException("Token expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info(String.format("Token %s unsupported", jwt.getToken()));
            throw new IllegalArgumentException("Token unsupported: " + e.getMessage());
        } catch (MalformedJwtException e) {
            log.info(String.format("Token %s malformed", jwt.getToken()));
            throw new IllegalArgumentException("Token malformed: " + e.getMessage());
        } catch (SignatureException e) {
            log.info(String.format("Token %s signature exception", jwt.getToken()));
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}

