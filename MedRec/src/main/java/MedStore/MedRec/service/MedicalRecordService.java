package MedStore.MedRec.service;

import MedStore.MedRec.dto.internal.UserDto;
import MedStore.MedRec.dto.outgoing.MedicalRecordDto;
import MedStore.MedRec.entities.MedicalRecord;
import MedStore.MedRec.enums.Role;
import MedStore.MedRec.repository.MedicalRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MedicalRecordService {

    private final Logger log = LoggerFactory.getLogger(MedicalRecordService.class);
    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public MedicalRecordDto getMedicalRecord(UserDto userDto, long medicalRecordId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByRecordId(medicalRecordId);

        if (medicalRecord == null) {
            log.error("Medical record " + medicalRecordId + " not found");
            throw new IllegalArgumentException("Medical record not found");
        } else if (hasRecordViewingRights(userDto, medicalRecord)) {
            log.info("User " + userDto.userId() + " accessed medical record " + medicalRecordId);
            return toDto(medicalRecord);
        } else {
            log.error("User" + userDto.userId() + "not authorized to access medical record " + medicalRecordId);
            throw new IllegalArgumentException("User not authorized to access this medical record");
        }
    }

    public List<MedicalRecordDto> getAllMedicalRecords(UserDto userDto) {
        List<MedicalRecord> medicalRecords = switch (userDto.role()) {
            case PATIENT -> medicalRecordRepository.findByPatientId(userDto.userId());
            case NURSE -> medicalRecordRepository.findByNurseIdAndDivisionId(userDto.userId(), userDto.divisionId());
            case DOCTOR -> medicalRecordRepository.findByDoctorIdAndDivisionId(userDto.userId(), userDto.divisionId());
            case GOVORG -> medicalRecordRepository.findAll();
            default -> throw new IllegalArgumentException("User not authorized to read medical records");
        };
        return medicalRecords.stream().map(this::toDto).toList();
    }

    private boolean hasRecordViewingRights(UserDto user, MedicalRecord medicalRecord) {
        boolean patientPredicate = user.role().equals(Role.PATIENT) && user.userId() == medicalRecord.getPatientId();
        boolean nursePredicate = user.role().equals(Role.NURSE) && user.userId() == medicalRecord.getNurseId();
        boolean doctorPredicate = user.role().equals(Role.DOCTOR) && user.userId() == medicalRecord.getDoctorId();
        boolean divisionPredicate = (user.role().equals(Role.DOCTOR) || user.role().equals(Role.NURSE)) && user.divisionId().equals(medicalRecord.getDivisionId());
        boolean nurseOrDoctorPredicate = nursePredicate || doctorPredicate || divisionPredicate;
        boolean govorgPredicate = user.role().equals(Role.GOVORG);
        return patientPredicate || nurseOrDoctorPredicate || govorgPredicate;
    }

    private MedicalRecordDto toDto(MedicalRecord medicalRecord) {
        return new MedicalRecordDto(medicalRecord.getRecordId(), medicalRecord.getPatientId(),
                medicalRecord.getNurseId(), medicalRecord.getDoctorId(), medicalRecord.getDivisionId(),
                medicalRecord.getNote());
    }
}
