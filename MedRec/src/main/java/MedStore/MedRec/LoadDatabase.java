package MedStore.MedRec;

import MedStore.MedRec.crypt.PasswordEncryptor;
import MedStore.MedRec.repository.DivisionRepository;
import MedStore.MedRec.repository.MedicalRecordRepository;
import MedStore.MedRec.repository.UserRepository;
import MedStore.MedRec.crypt.Salt;
import MedStore.MedRec.entities.Division;
import MedStore.MedRec.entities.MedicalRecord;
import MedStore.MedRec.entities.User;
import MedStore.MedRec.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, MedicalRecordRepository medicalRecordRepository,
            DivisionRepository divisionRepository) {
        return args -> {
            User admin = createAdmin();
            log.info("Preloading " + userRepository.save(admin));
            Division division = createDivision();
            log.info("Preloading" + divisionRepository.saveAndFlush(division));
            User patient = createPatient();
            User nurse = createNurse(division.getDivisionId());
            User doctor = createDoctor(division.getDivisionId());
            User govOrg = createGovorg();
            log.info("Preloading " + userRepository.save(patient));
            log.info("Preloading " + userRepository.save(nurse));
            log.info("Preloading " + userRepository.save(doctor));
            log.info("Preloading " + userRepository.saveAndFlush(govOrg));
            MedicalRecord medicalRecord = createMedicalRecord(patient.getUserId(), nurse.getUserId(),
                    doctor.getUserId(), division.getDivisionId());
            log.info("Preloading " + medicalRecordRepository.save(medicalRecord));
        };
    }

    private User createAdmin() {
        User user = new User();
        user.setUsername("da4136st-s@student.lu.se");
        Salt salt = new Salt();
        user.setSalt(salt.getSalt());
        user.setPasswordhash(PasswordEncryptor.encrypt("weakPassword", salt.getSalt()));
        user.setRole(Role.ADMIN);
        user.setDivisionId(null);
        return user;
    }

    private User createPatient() {
        User user = new User();
        user.setUsername("ya4652al-s@studen.lu.se");
        Salt salt = new Salt();
        user.setSalt(salt.getSalt());
        user.setPasswordhash(PasswordEncryptor.encrypt("weakPassword", salt.getSalt()));
        user.setRole(Role.ADMIN);
        return user;

    }

    private User createNurse(long divisionId) {
        User user = new User();
        user.setUsername("ad3122st-s@student.lu.se");
        Salt salt = new Salt();
        user.setSalt(salt.getSalt());
        user.setPasswordhash(PasswordEncryptor.encrypt("weakPassword", salt.getSalt()));
        user.setRole(Role.NURSE);
        user.setDivisionId(divisionId);
        return user;

    }

    private User createDoctor(long divisionId) {
        User user = new User();
        user.setUsername("da4136st-s@student.lu.se");
        Salt salt = new Salt();
        user.setSalt(salt.getSalt());
        user.setPasswordhash(PasswordEncryptor.encrypt("weakPassword", salt.getSalt()));
        user.setRole(Role.DOCTOR);
        user.setDivisionId(divisionId);
        return user;
    }

    private User createGovorg() {
        User user = new User();
        user.setUsername("da4136st-s@student.lu.se");
        Salt salt = new Salt();
        user.setSalt(salt.getSalt());
        user.setPasswordhash(PasswordEncryptor.encrypt("weakPassword", salt.getSalt()));
        user.setRole(Role.GOVORG);
        return user;
    }

    private Division createDivision() {
        Division division = new Division();
        division.setDivisionName("First Division");
        return division;
    }

    private MedicalRecord createMedicalRecord(long patientId, long nurseId, long doctorId, long divisionId) {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatientId(patientId);
        medicalRecord.setNurseId(nurseId);
        medicalRecord.setDoctorId(doctorId);
        medicalRecord.setDivisionId(divisionId);
        medicalRecord.setNote("THIS IS A CERTIFIED HOOD CLASSIC");
        return medicalRecord;
    }

}