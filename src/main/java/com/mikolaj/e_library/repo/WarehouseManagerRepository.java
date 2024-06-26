package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.User;
import com.mikolaj.e_library.model.WarehouseManager;
import com.mikolaj.e_library.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseManagerRepository extends JpaRepository<WarehouseManager, Integer> {
    Optional<WarehouseManager> findByUserEmail(String email);
    Optional<WarehouseManager> findByUserId(Integer userId);
    boolean existsByUserEmail(String email);
    boolean existsByUserId(int userId);

}