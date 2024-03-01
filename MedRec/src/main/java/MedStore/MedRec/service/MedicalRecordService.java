package MedStore.MedRec.service;

import MedStore.MedRec.dto.incoming.UpdateMedicalRecordDto;
import MedStore.MedRec.dto.internal.UserDto;
import MedStore.MedRec.dto.outgoing.CreateMedicalRecordDto;
import MedStore.MedRec.dto.outgoing.MedicalRecordDto;
import MedStore.MedRec.entities.MedicalRecord;
import MedStore.MedRec.enums.Role;
import MedStore.MedRec.repository.MedicalRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    private final Logger log = LoggerFactory.getLogger(MedicalRecordService.class);

    public MedicalRecordDto getMedicalRecord(UserDto userDto, long medicalRecordId) {
        MedicalRecord medicalRecord =
                medicalRecordRepository
                        .findById(medicalRecordId)
                        .orElseThrow(() -> {
                            log.error("Medical record " + medicalRecordId + " not found");
                            return new IllegalArgumentException("Medical record not found");
                        });
        if (hasRecordViewingRights(userDto, medicalRecord)) {
            log.info("User " + userDto.userId() + " accessed medical record " + medicalRecordId);
            return toDto(medicalRecord);
        } else {
            log.error("User" + userDto.userId() + "not authorized to access medical record " + medicalRecordId);
            throw new IllegalArgumentException("User not authorized to access this medical record");
        }
    }

    public CreateMedicalRecordDto createMedicalRecord(UserDto user, long patientId, long nurseId, String note) {
        if (user.role().equals(Role.DOCTOR)) {
            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setDivisionId(user.divisionId());
            medicalRecord.setDoctorId(user.userId());
            medicalRecord.setNote(note);
            medicalRecord.setNurseId(nurseId);
            medicalRecord.setPatientId(patientId);
            medicalRecordRepository.save(medicalRecord);
            return createDto(medicalRecord);
        } else {
            log.error("User" + user.userId() + "not authorized to access medical record ");
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

    public void updateMedicalRecord(UserDto userDto, UpdateMedicalRecordDto updateMedicalRecordDto) {
        long medicalRecordId = updateMedicalRecordDto.recordId();
        MedicalRecord medicalRecord =
                medicalRecordRepository
                        .findById(medicalRecordId)
                        .orElseThrow(() ->{
                            log.error("Medical record " + medicalRecordId + " not found");
                            return new IllegalArgumentException("Medical record not found");
                        });
        if (hasRecordWritingRights(userDto, medicalRecord)) {
            medicalRecord.setNote(updateMedicalRecordDto.note());
            medicalRecordRepository.save(medicalRecord);
            log.info("User " + userDto.userId() + " updated medical record " + medicalRecordId);
        } else {
            log.error("User " + userDto.userId() + " not authorized to update medical record " + medicalRecordId);
            throw new IllegalArgumentException("User not authorized to update medical record");
        }
    }

    public void deleteMedicalRecord(UserDto userDto, long medicalRecordId) {
        if (userDto.role().equals(Role.GOVORG)) {
            log.info("User " + userDto.userId() + " deleted medical record " + medicalRecordId);
            medicalRecordRepository.deleteById(medicalRecordId);
        } else {
            log.error("User " + userDto.userId() + " not authorized to delete medical record " + medicalRecordId);
            throw new IllegalArgumentException("User not authorized to delete medical record");
        }
    }

    private boolean hasRecordWritingRights(UserDto user, MedicalRecord medicalRecord) {
        boolean isNurse = user.role().equals(Role.NURSE);
        boolean isDoctor = user.role().equals(Role.DOCTOR);
        boolean nursePredicate = isNurse && user.userId() == medicalRecord.getNurseId();
        boolean doctorPredicate = isDoctor && user.userId() == medicalRecord.getDoctorId();
        return nursePredicate || doctorPredicate;
    }

    private boolean hasRecordViewingRights(UserDto user, MedicalRecord medicalRecord) {
        boolean patientPredicate = user.role().equals(Role.PATIENT) && user.userId() == medicalRecord.getPatientId();
        boolean govorgPredicate = user.role().equals(Role.GOVORG);

        boolean isNurse = user.role().equals(Role.NURSE);
        boolean isDoctor = user.role().equals(Role.DOCTOR);
        boolean divisionPredicate = (isDoctor || isNurse) && user.divisionId().equals(medicalRecord.getDivisionId());

        return hasRecordWritingRights(user, medicalRecord) || patientPredicate || govorgPredicate || divisionPredicate;
    }

    private MedicalRecordDto toDto(MedicalRecord medicalRecord) {
        return
                new MedicalRecordDto(
                        medicalRecord.getRecordId(),
                        medicalRecord.getPatientId(),
                        medicalRecord.getNurseId(),
                        medicalRecord.getDoctorId(),
                        medicalRecord.getDivisionId(),
                        medicalRecord.getNote()
                );
    }

    private CreateMedicalRecordDto createDto(MedicalRecord medicalRecord) {
        return new CreateMedicalRecordDto(medicalRecord.getRecordId(), medicalRecord.getPatientId(),
                medicalRecord.getNurseId(), medicalRecord.getDoctorId(), medicalRecord.getDivisionId(),
                medicalRecord.getNote());
    }
}
