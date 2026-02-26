package com.it342.timesheets.controller;

import com.it342.timesheets.dto.TimesheetDashboardResponse;
import com.it342.timesheets.service.TimesheetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/timesheets")
public class TimesheetController {

    private final TimesheetService timesheetService;

    public TimesheetController(TimesheetService timesheetService) {
        this.timesheetService = timesheetService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<TimesheetDashboardResponse> getDashboard(Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(timesheetService.getDashboard(userId));
    }

    @PostMapping("/clock-in")
    public ResponseEntity<Void> clockIn(Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        timesheetService.clockIn(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clock-out")
    public ResponseEntity<Void> clockOut(Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        timesheetService.clockOut(userId);
        return ResponseEntity.ok().build();
    }
}
