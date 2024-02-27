package MedStore.MedRec;

import MedStore.MedRec.crypt.PasswordEncryptor;
import MedStore.MedRec.repository.UserRepository;
import MedStore.MedRec.crypt.Salt;
import MedStore.MedRec.entities.User;
import MedStore.MedRec.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        User user = new User();
        user.setUsername("Admin");
        Salt salt = new Salt();
        user.setSalt(salt.getSalt());
        user.setPasswordhash(PasswordEncryptor.encrypt("weakPassword", salt.getSalt()));
        user.setRole(Role.ADMIN);
        user.setDivisionId(1L);
        return args -> {
            log.info("Preloading " + userRepository.save(user));
        };
    }
}