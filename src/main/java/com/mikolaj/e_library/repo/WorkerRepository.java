package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.Reader;
import com.mikolaj.e_library.model.User;
import com.mikolaj.e_library.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Integer> {
    Optional<Worker> findByUserEmail(String email);
    Optional<Worker> findByUserId(int id);
    boolean existsByUserEmail(String email);
    boolean existsByUserId(int userId);

}