package MedStore.MedRec.controller;

import MedStore.MedRec.dto.incoming.UpdateMedicalRecordDto;
import MedStore.MedRec.dto.internal.UserDto;
import MedStore.MedRec.dto.outgoing.CreateMedicalRecordDto;
import MedStore.MedRec.dto.outgoing.MedicalRecordDto;
import MedStore.MedRec.entities.MedicalRecord;
import MedStore.MedRec.service.AuthenticationService;
import MedStore.MedRec.service.MedicalRecordService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MedicalRecordController {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private MedicalRecordService medicalRecordService;

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordController.class);

    @GetMapping("/users/me/medical-record/{medicalRecordId}")
    public MedicalRecordDto getMedicalRecord(
            HttpServletRequest request,
            @PathVariable("medicalRecordId") long medicalRecordId) throws BadRequestException {
        log.info("Medical record request received, requestId: " + request.getRequestId());
        try {
            UserDto userDto = authenticationService.validateJWT(request);
            return medicalRecordService.getMedicalRecord(userDto, medicalRecordId);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Request");
        }

    }

    @GetMapping("/users/me/medical-record")
    public List<MedicalRecordDto> getAllMedicalRecords(HttpServletRequest request) throws BadRequestException {
        log.info("Medical record request received, requestId: " + request.getRequestId());
        try {
            UserDto userDto = authenticationService.validateJWT(request);
            return medicalRecordService.getAllMedicalRecords(userDto);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Request");
        }
    }

    @PutMapping("/users/me/medical-record/")
    public void updateMedicalRecord(HttpServletRequest request, @RequestBody UpdateMedicalRecordDto updateMedicalRecordDto) throws BadRequestException {
        log.info("Medical record request received, requestId: " + request.getRequestId());
        validateUpdateMedicalRecordDto(updateMedicalRecordDto);
        try {
            UserDto userDto = authenticationService.validateJWT(request);
            medicalRecordService.updateMedicalRecord(userDto, updateMedicalRecordDto);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Request");
        }
    }

    @PostMapping("/users/me/medical-record/create")
    public CreateMedicalRecordDto createMedicalRecord(HttpServletRequest request,
                                                      @RequestBody MedicalRecord medicalRecord)
            throws BadRequestException {
        try {
            log.info("Create medical record received, userId" + request.getRequestId());
            UserDto userDto = authenticationService.validateJWT(request);
            return medicalRecordService.createMedicalRecord(userDto, medicalRecord.getPatientId(),
                    medicalRecord.getNurseId(),
                    medicalRecord.getNote());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Request");
        }
    }

    @DeleteMapping("/users/me/medical-record/{medicalRecordId}")
    public void deleteMedicalRecord(HttpServletRequest request, @PathVariable("medicalRecordId") long medicalRecordId) throws BadRequestException {
        log.info("Medical record request received, requestId: " + request.getRequestId());
        try {
            UserDto userDto = authenticationService.validateJWT(request);
            medicalRecordService.deleteMedicalRecord(userDto, medicalRecordId);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Request");
        }
    }

    private void validateUpdateMedicalRecordDto(UpdateMedicalRecordDto updateMedicalRecordDto) throws BadRequestException {
        if (updateMedicalRecordDto == null || updateMedicalRecordDto.recordId() == 0 || updateMedicalRecordDto.note().isBlank()) {
            throw new BadRequestException("Invalid Request");
        }
    }

}
