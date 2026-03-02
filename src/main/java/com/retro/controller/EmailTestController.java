package com.retro.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.service.EmailService;

@RestController
@RequestMapping("/api/test")
@CrossOrigin("http://localhost:5173")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    // Test email endpoint
    @PostMapping("/email")
    public ResponseEntity<Map<String, String>> testEmail(@RequestParam String to) {
        Map<String, String> response = new HashMap<>();
        
        try {
            emailService.sendTestEmail(to);
            response.put("status", "success");
            response.put("message", "Test email sent successfully to " + to);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to send email: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Check email configuration
    @GetMapping("/email/config")
    public ResponseEntity<Map<String, String>> checkEmailConfig() {
        Map<String, String> config = new HashMap<>();
        
        // Read from application.properties (don't expose password)
        config.put("host", "smtp.gmail.com");
        config.put("port", "587");
        config.put("username", "saivijjivijji@gmail.com");
        config.put("auth", "enabled");
        config.put("starttls", "enabled");
        config.put("status", "configured");
        
        return ResponseEntity.ok(config);
    }
}
