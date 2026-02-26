package com.it342.timesheets.repository;

import com.it342.timesheets.entity.TimesheetRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimesheetRecordRepository extends JpaRepository<TimesheetRecord, Integer> {

    Optional<TimesheetRecord> findFirstByEmployee_EmployeeIdAndStatusOrderByClockInAtDesc(Integer employeeId, String status);

    List<TimesheetRecord> findByCreatedByUser_UserIdOrderByClockInAtDesc(Integer employerId);

    List<TimesheetRecord> findByEmployee_EmployeeIdOrderByClockInAtDesc(Integer employeeId);
}
