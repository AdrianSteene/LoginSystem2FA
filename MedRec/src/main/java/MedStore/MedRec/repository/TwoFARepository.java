package MedStore.MedRec.repository;

import MedStore.MedRec.entities.TwoFA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwoFARepository extends JpaRepository<TwoFA, Long> {
    TwoFA findByUserIdAndTwoFACode(long userId, String twoFACode);
}