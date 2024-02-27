package MedStore.MedRec;

import MedStore.MedRec.repository.UserRepository;
import MedStore.MedRec.crypt.Salt;
import MedStore.MedRec.entities.User;
import MedStore.MedRec.enums.Role;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        User user = new User();
        user.setUsername("Admin");
        Salt salt = new Salt();
        user.setSalt(salt.getSalt());
        user.setPasswordhash(
                Hashing.sha256()
                        .hashString("weakPassword" + salt.getSalt(),
                                StandardCharsets.UTF_8)
                        .toString());
        user.setRole(Role.ADMIN);
        user.setDivisionId(1L);
        return args -> {
            log.info("Preloading " + userRepository.save(user));
        };
    }
}