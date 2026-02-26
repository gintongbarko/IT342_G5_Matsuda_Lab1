package com.it342.timesheets.service;

import com.it342.timesheets.dto.TimesheetDashboardResponse;
import com.it342.timesheets.dto.TimesheetRecordResponse;
import com.it342.timesheets.entity.Employee;
import com.it342.timesheets.entity.TimesheetRecord;
import com.it342.timesheets.entity.User;
import com.it342.timesheets.entity.UserRole;
import com.it342.timesheets.repository.EmployeeRepository;
import com.it342.timesheets.repository.TimesheetRecordRepository;
import com.it342.timesheets.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TimesheetService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final TimesheetRecordRepository timesheetRecordRepository;

    public TimesheetService(UserRepository userRepository,
                            EmployeeRepository employeeRepository,
                            TimesheetRecordRepository timesheetRecordRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.timesheetRecordRepository = timesheetRecordRepository;
    }

    @Transactional(readOnly = true)
    public TimesheetDashboardResponse getDashboard(Integer userId) {
        User user = getUser(userId);

        if (user.getRole() == UserRole.EMPLOYER) {
            List<String> employees = employeeRepository.findByCreatedByUser_UserIdAndIsActiveTrueOrderByEmployeeNameAsc(userId)
                    .stream()
                .map(Employee::getEmployeeName)
                    .toList();
            List<TimesheetRecordResponse> records = timesheetRecordRepository.findByCreatedByUser_UserIdOrderByClockInAtDesc(userId)
                    .stream()
                    .map(this::toRecordResponse)
                    .toList();
            return new TimesheetDashboardResponse(user.getRole().name(), null, BigDecimal.ZERO, false, employees, records);
        }

        if (user.getEmployer() == null) {
            throw new RuntimeException("Employee account is missing employer assignment");
        }

        Employee employee = getEmployeeForUser(user);

        List<TimesheetRecordResponse> records = timesheetRecordRepository.findByEmployee_EmployeeIdOrderByClockInAtDesc(employee.getEmployeeId())
                .stream()
                .map(this::toRecordResponse)
                .toList();

        BigDecimal accumulatedHours = records.stream()
                .map(TimesheetRecordResponse::getHoursWorked)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean clockedIn = timesheetRecordRepository
            .findFirstByEmployee_EmployeeIdAndStatusOrderByClockInAtDesc(employee.getEmployeeId(), "clocked_in")
            .isPresent();

        return new TimesheetDashboardResponse(
                user.getRole().name(),
                user.getEmployer().getUsername(),
                accumulatedHours,
                clockedIn,
                List.of(),
                records
        );
    }

    @Transactional
    public void clockIn(Integer userId) {
        User user = getUser(userId);
        requireEmployee(user);
        Employee employee = getEmployeeForUser(user);

        if (timesheetRecordRepository.findFirstByEmployee_EmployeeIdAndStatusOrderByClockInAtDesc(employee.getEmployeeId(), "clocked_in").isPresent()) {
            throw new RuntimeException("Already clocked in");
        }

        TimesheetRecord record = new TimesheetRecord();
        record.setEmployee(employee);
        record.setCreatedByUser(user.getEmployer());
        record.setClockInAt(LocalDateTime.now());
        record.setStatus("clocked_in");
        timesheetRecordRepository.save(record);
    }

    @Transactional
    public void clockOut(Integer userId) {
        User user = getUser(userId);
        requireEmployee(user);
        Employee employee = getEmployeeForUser(user);

        TimesheetRecord record = timesheetRecordRepository
            .findFirstByEmployee_EmployeeIdAndStatusOrderByClockInAtDesc(employee.getEmployeeId(), "clocked_in")
                .orElseThrow(() -> new RuntimeException("No active clock-in record found"));

        LocalDateTime clockOutAt = LocalDateTime.now();
        BigDecimal minutesWorked = BigDecimal.valueOf(Duration.between(record.getClockInAt(), clockOutAt).toMinutes());
        BigDecimal hoursWorked = minutesWorked.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        record.setClockOutAt(clockOutAt);
        record.setHoursWorked(hoursWorked);
        record.setStatus("clocked_out");
        timesheetRecordRepository.save(record);
    }

    private Employee getEmployeeForUser(User user) {
        if (user.getEmployer() == null) {
            throw new RuntimeException("Employee account is missing employer assignment");
        }
        return employeeRepository
                .findTopByEmployeeNameAndCreatedByUser_UserIdOrderByEmployeeIdDesc(user.getUsername(), user.getEmployer().getUserId())
                .orElseThrow(() -> new RuntimeException("Employee record not found"));
    }

    private User getUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void requireEmployee(User user) {
        if (user.getRole() != UserRole.EMPLOYEE) {
            throw new RuntimeException("Only employees can clock in/out");
        }
        if (user.getEmployer() == null) {
            throw new RuntimeException("Employee account is missing employer assignment");
        }
    }

    private TimesheetRecordResponse toRecordResponse(TimesheetRecord record) {
        return new TimesheetRecordResponse(
                record.getRecordId(),
                record.getEmployee().getEmployeeName(),
                record.getCreatedByUser().getUsername(),
                record.getClockInAt(),
                record.getClockOutAt(),
                record.getHoursWorked()
        );
    }
}
