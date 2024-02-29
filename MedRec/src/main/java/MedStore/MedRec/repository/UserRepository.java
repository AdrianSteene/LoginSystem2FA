package MedStore.MedRec.repository;

import MedStore.MedRec.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByUserId(long userId);
}
