package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
import com.project.back_end.services.Service;

@Controller
public class PatientDashboardController {

    @Autowired
    private Service service;

    @GetMapping("/patientDashboard/{token}")
    public String patientDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");

        if (validation.getStatusCode().is2xxSuccessful()) {
            return "redirect:/pages/patientDashboard.html";
        }
        return "redirect:http://localhost:8080";
    }

}
