package MedStore.MedRec.repository;

import MedStore.MedRec.entities.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Long> {
}
