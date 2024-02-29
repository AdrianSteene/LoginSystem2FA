package MedStore.MedRec.service;

import MedStore.MedRec.entities.User;
import MedStore.MedRec.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenericService {
    @Autowired
    private UserRepository userRepository;
    protected static final Logger log = LoggerFactory.getLogger(GenericService.class);

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUser(long userId) {
        return userRepository.findByUserId(userId);
    }
}
