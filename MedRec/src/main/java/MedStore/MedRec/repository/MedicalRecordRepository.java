package MedStore.MedRec.repository;

import MedStore.MedRec.entities.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    MedicalRecord findByRecordId(long recordId);
    MedicalRecord findByPatientId(long patientId);
    MedicalRecord findByNurseId(long nurseId);
    MedicalRecord findByDoctorId(long doctorId);
    MedicalRecord findByDivisionId(long divisionId);
    MedicalRecord findByNote(String note);
}
