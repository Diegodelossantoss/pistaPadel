package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/pistaPadel/health")
    public String health() {
        return "OK"; // 200 OK
    }
}