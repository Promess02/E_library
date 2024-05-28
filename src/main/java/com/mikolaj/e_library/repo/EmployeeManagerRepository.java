package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.EmployeeManager;
import com.mikolaj.e_library.model.User;
import com.mikolaj.e_library.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeManagerRepository extends JpaRepository<EmployeeManager, Integer> {
    Optional<EmployeeManager> findByUserEmail(String email);
    boolean existsByUserId(int userId);
    boolean existsByUserEmail(String email);
}