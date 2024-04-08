package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.EmployeeManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeManagerRepository extends JpaRepository<EmployeeManager, Integer> {
}