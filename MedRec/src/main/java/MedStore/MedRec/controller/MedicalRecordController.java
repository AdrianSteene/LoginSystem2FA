package MedStore.MedRec.controller;

import MedStore.MedRec.dto.internal.UserDto;
import MedStore.MedRec.dto.outgoing.MedicalRecordDto;
import MedStore.MedRec.entities.MedicalRecord;
import MedStore.MedRec.enums.Role;
import MedStore.MedRec.repository.LoginRepository;
import MedStore.MedRec.repository.MedicalRecordRepository;
import MedStore.MedRec.repository.UserRepository;
import MedStore.MedRec.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MedicalRecordController {

    private final AuthenticationService authenticationService;
    private final MedicalRecordRepository medicalRecordRepository;

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordController.class);

    public MedicalRecordController(UserRepository userRepository, LoginRepository loginRepository,
            MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.authenticationService = new AuthenticationService(userRepository, loginRepository);
    }

    @GetMapping("/users/me/medical-record/{medicalRecordId}")
    public MedicalRecordDto getMedicalRecord(HttpServletRequest request,
            @PathVariable("medicalRecordId") long medicalRecordId) throws BadRequestException {

        UserDto userDto = authenticationService.validateJWT(request);
        log.info("Request for medical record " + medicalRecordId);
        MedicalRecord medicalRecord = medicalRecordRepository.findByRecordId(medicalRecordId);

        if (medicalRecord == null) {
            log.error("Medical record " + medicalRecordId + " not found");
            throw new BadRequestException("Medical record not found");
        }

        if (hasRecordViewingRights(userDto, medicalRecord)) {
            return new MedicalRecordDto(medicalRecord.getRecordId(), medicalRecord.getPatientId(),
                    medicalRecord.getNurseId(), medicalRecord.getDoctorId(), medicalRecord.getDivisionId(),
                    medicalRecord.getNote());
        } else {
            log.error("User" + userDto.userId() + "not authorized to access medical record " + medicalRecordId);
            throw new BadRequestException("User not authorized to access this medical record");
        }
    }

    @GetMapping("/users/me/medical-record")
    public List<MedicalRecordDto> getAllMedicalRecords(HttpServletRequest request) throws BadRequestException {
        UserDto userDto = authenticationService.validateJWT(request);
        List<MedicalRecord> medicalRecords;
        if (userDto.role().equals(Role.PATIENT)) {
            medicalRecords = medicalRecordRepository.findByPatientId(userDto.userId());
        } else if (userDto.role().equals(Role.NURSE)) {
            medicalRecords = medicalRecordRepository.findByNurseIdAndDivisionId(userDto.userId(), userDto.divisionId());
        } else if (userDto.role().equals(Role.DOCTOR)) {
            medicalRecords = medicalRecordRepository.findByDoctorIdAndDivisionId(userDto.userId(),
                    userDto.divisionId());
        } else if (userDto.role().equals(Role.GOVORG)) {
            medicalRecords = medicalRecordRepository.findAll();
        } else {
            throw new BadRequestException("User not authorized to read medical records");
        }

        return medicalRecords.stream()
                .map(medicalRecord -> new MedicalRecordDto(medicalRecord.getRecordId(),
                        medicalRecord.getPatientId(), medicalRecord.getNurseId(), medicalRecord.getDoctorId(),
                        medicalRecord.getDivisionId(), medicalRecord.getNote()))
                .toList();

    }

    private boolean hasRecordViewingRights(UserDto user, MedicalRecord medicalRecord) {
        boolean patientPredicate = user.role().equals(Role.PATIENT) && user.userId() == medicalRecord.getPatientId();
        boolean nurseOrDoctorPredicate = (user.role().equals(Role.NURSE) && user.userId() == medicalRecord.getNurseId())
                ||
                (user.role().equals(Role.DOCTOR) && user.userId() == medicalRecord.getDoctorId()) ||
                ((user.role().equals(Role.DOCTOR) || user.role().equals(Role.NURSE))
                        && user.divisionId().equals(medicalRecord.getDivisionId()));
        boolean govorgPredicate = user.role().equals(Role.GOVORG);
        return patientPredicate || nurseOrDoctorPredicate || govorgPredicate;
    }
}
