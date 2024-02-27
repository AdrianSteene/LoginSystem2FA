package MedStore.MedRec.repository;

import MedStore.MedRec.entities.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long>{
}
