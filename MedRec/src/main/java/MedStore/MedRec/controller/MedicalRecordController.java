package MedStore.MedRec.controller;

import MedStore.MedRec.dto.internal.UserDto;
import MedStore.MedRec.dto.outgoing.MedicalRecordDto;
import MedStore.MedRec.entities.MedicalRecord;
import MedStore.MedRec.enums.Role;
import MedStore.MedRec.repository.LoginRepository;
import MedStore.MedRec.repository.MedicalRecordRepository;
import MedStore.MedRec.repository.TwoFARepository;
import MedStore.MedRec.repository.UserRepository;
import MedStore.MedRec.service.AuthenticationService;
import MedStore.MedRec.service.MedicalRecordService;
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
    private final MedicalRecordService medicalRecordService;

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordController.class);

    public MedicalRecordController(UserRepository userRepository, LoginRepository loginRepository,
            MedicalRecordRepository medicalRecordRepository, TwoFARepository twoFARepository) {
        this.medicalRecordService = new MedicalRecordService(medicalRecordRepository);
        this.authenticationService = new AuthenticationService(userRepository, loginRepository, twoFARepository);
    }

    @GetMapping("/users/me/medical-record/{medicalRecordId}")
    public MedicalRecordDto getMedicalRecord(HttpServletRequest request,
            @PathVariable("medicalRecordId") long medicalRecordId) throws BadRequestException {
        log.info("Medical record request received, requestId: " + request.getRequestId());
        UserDto userDto = authenticationService.validateJWT(request);
        try {
            return medicalRecordService.getMedicalRecord(userDto, medicalRecordId);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Request");
        }

    }

    @GetMapping("/users/me/medical-record")
    public List<MedicalRecordDto> getAllMedicalRecords(HttpServletRequest request) throws BadRequestException {
        log.info("Medical record request received, requestId: " + request.getRequestId());
        UserDto userDto = authenticationService.validateJWT(request);
        try {
            return medicalRecordService.getAllMedicalRecords(userDto);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Request");
        }
    }
}
