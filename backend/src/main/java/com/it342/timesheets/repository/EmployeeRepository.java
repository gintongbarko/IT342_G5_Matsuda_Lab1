package com.it342.timesheets.repository;

import com.it342.timesheets.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    List<Employee> findByCreatedByUser_UserIdAndIsActiveTrueOrderByEmployeeNameAsc(Integer employerId);

    Optional<Employee> findTopByEmployeeNameAndCreatedByUser_UserIdOrderByEmployeeIdDesc(String employeeName, Integer employerId);
}
