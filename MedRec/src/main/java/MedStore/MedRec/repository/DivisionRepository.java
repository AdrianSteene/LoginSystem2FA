package MedStore.MedRec.repository;

import MedStore.MedRec.entities.Division;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DivisionRepository extends JpaRepository<Division, Long> {
}
