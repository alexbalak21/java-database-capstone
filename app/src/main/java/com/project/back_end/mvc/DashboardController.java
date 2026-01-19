package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
import com.project.back_end.services.Service;

@Controller
public class DashboardController {

    @Autowired
    private Service service;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");

        if (validation.getStatusCode().is2xxSuccessful()) {
            return "admin/adminDashboard";
        }
        return "redirect:http://localhost:8080";
    }

    @GetMapping("/adminDashboard")
    public String adminDashboardPlain() {
        // Token validation is handled by API calls from the page via Authorization headers.
        return "admin/adminDashboard";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");

        if (validation.getStatusCode().is2xxSuccessful()) {
            return "doctor/doctorDashboard";
        }
        return "redirect:http://localhost:8080";
    }

    @GetMapping("/doctorDashboard")
    public String doctorDashboardPlain() {
        // Token validation is handled by API calls from the page via Authorization headers.
        return "doctor/doctorDashboard";
    }

}
