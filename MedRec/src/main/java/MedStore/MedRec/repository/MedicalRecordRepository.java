package MedStore.MedRec.repository;

import MedStore.MedRec.entities.MedicalRecord;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    MedicalRecord findByRecordId(long recordId);

    List<MedicalRecord> findByPatientId(long patientId);

    List<MedicalRecord> findByNurseId(long nurseId);

    List<MedicalRecord> findByDoctorId(long doctorId);

    List<MedicalRecord> findByDivisionId(long divisionId);

    List<MedicalRecord> findByNurseIdAndDivisionId(long nurseId, long divisionId);

    List<MedicalRecord> findByDoctorIdAndDivisionId(long doctorId, long divisionId);
}
