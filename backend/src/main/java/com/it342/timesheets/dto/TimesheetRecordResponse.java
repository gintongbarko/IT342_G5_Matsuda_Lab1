package com.it342.timesheets.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TimesheetRecordResponse {

    private Integer recordId;
    private String employeeName;
    private String employerName;
    private LocalDateTime clockInAt;
    private LocalDateTime clockOutAt;
    private BigDecimal hoursWorked;

    public TimesheetRecordResponse(Integer recordId,
                                   String employeeName,
                                   String employerName,
                                   LocalDateTime clockInAt,
                                   LocalDateTime clockOutAt,
                                   BigDecimal hoursWorked) {
        this.recordId = recordId;
        this.employeeName = employeeName;
        this.employerName = employerName;
        this.clockInAt = clockInAt;
        this.clockOutAt = clockOutAt;
        this.hoursWorked = hoursWorked;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmployerName() {
        return employerName;
    }

    public LocalDateTime getClockInAt() {
        return clockInAt;
    }

    public LocalDateTime getClockOutAt() {
        return clockOutAt;
    }

    public BigDecimal getHoursWorked() {
        return hoursWorked;
    }
}
