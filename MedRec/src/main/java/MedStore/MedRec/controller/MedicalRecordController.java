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
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MedicalRecordController {

    private final AuthenticationService authenticationService;
    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordController(UserRepository userRepository, LoginRepository loginRepository, MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.authenticationService = new AuthenticationService(userRepository, loginRepository);
    }


    @GetMapping("/users/{medicalRecordId}/medical-record")
    public MedicalRecordDto getMedicalRecord(HttpServletRequest request, @PathVariable("medicalRecordId") long medicalRecordId) throws BadRequestException {
        UserDto userDto = authenticationService.validateJWT(request);
        MedicalRecord medicalRecord = medicalRecordRepository.findByRecordId(medicalRecordId);
        if (isAuthorized(userDto, medicalRecord)) {
            return new MedicalRecordDto(medicalRecord.getRecordId(), medicalRecord.getPatientId(), medicalRecord.getNurseId(), medicalRecord.getDoctorId(), medicalRecord.getDivisionId(), medicalRecord.getNote());
        } else {
            throw new BadRequestException("User not authorized to access this medical record");
        }
    }

    private boolean isAuthorized(UserDto user, MedicalRecord medicalRecord) {
        boolean patientPredicate = user.role().equals(Role.PATIENT) && user.userId() == medicalRecord.getPatientId();
        boolean nurseOrDoctorPredicate = (user.role().equals(Role.NURSE) || user.role().equals(Role.DOCTOR)) && (user.userId() == medicalRecord.getNurseId() || user.divisionId() == medicalRecord.getDivisionId());
        boolean govorgPredicate = user.role().equals(Role.GOVORG);
        return patientPredicate || nurseOrDoctorPredicate || govorgPredicate;
    }
}
