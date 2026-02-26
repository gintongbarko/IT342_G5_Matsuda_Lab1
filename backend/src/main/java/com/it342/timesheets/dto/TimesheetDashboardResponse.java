package com.it342.timesheets.dto;

import java.math.BigDecimal;
import java.util.List;

public class TimesheetDashboardResponse {

    private String role;
    private String employerName;
    private BigDecimal accumulatedHours;
    private boolean clockedIn;
    private List<String> employees;
    private List<TimesheetRecordResponse> records;

    public TimesheetDashboardResponse(String role,
                                      String employerName,
                                      BigDecimal accumulatedHours,
                                      boolean clockedIn,
                                      List<String> employees,
                                      List<TimesheetRecordResponse> records) {
        this.role = role;
        this.employerName = employerName;
        this.accumulatedHours = accumulatedHours;
        this.clockedIn = clockedIn;
        this.employees = employees;
        this.records = records;
    }

    public String getRole() {
        return role;
    }

    public String getEmployerName() {
        return employerName;
    }

    public BigDecimal getAccumulatedHours() {
        return accumulatedHours;
    }

    public boolean isClockedIn() {
        return clockedIn;
    }

    public List<String> getEmployees() {
        return employees;
    }

    public List<TimesheetRecordResponse> getRecords() {
        return records;
    }
}
