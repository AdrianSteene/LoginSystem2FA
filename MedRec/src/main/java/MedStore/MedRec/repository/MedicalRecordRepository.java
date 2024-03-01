package MedStore.MedRec.repository;

import MedStore.MedRec.entities.MedicalRecord;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientId(long patientId);

    List<MedicalRecord> findByNurseIdAndDivisionId(long nurseId, long divisionId);

    List<MedicalRecord> findByDoctorIdAndDivisionId(long doctorId, long divisionId);
}
