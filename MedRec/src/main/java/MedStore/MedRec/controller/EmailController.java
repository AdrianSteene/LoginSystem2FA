package MedStore.MedRec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import MedStore.MedRec.service.EmailService;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/sendEmail/{email}")
    public String sendEmail(@PathVariable String email) {
        emailService.sendSimpleMessage("adrian.steene.3122@user.dsek.se", "Test Subject",
                "Hello, this is a test email.");
        return "Email sent successfully";
    }
}
