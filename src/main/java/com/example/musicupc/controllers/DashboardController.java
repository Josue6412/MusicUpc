package com.example.musicupc.controllers;

import com.example.musicupc.dtos.DashboardDTO;
import com.example.musicupc.services.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public DashboardDTO resumen(@RequestParam(defaultValue = "todo") String periodo) {
        return dashboardService.obtenerResumen(periodo);
    }
}