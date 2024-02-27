package MedStore.MedRec.service;

import MedStore.MedRec.entities.User;
import MedStore.MedRec.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericService {
    protected static final Logger log = LoggerFactory.getLogger(GenericService.class);
    private final UserRepository userRepository;

    public GenericService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }
}
