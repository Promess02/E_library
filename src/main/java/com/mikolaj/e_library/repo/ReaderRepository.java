package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReaderRepository extends JpaRepository<Reader, Integer> {
    Optional<Reader> findByUserEmail(String email);
    boolean existsByUserEmail(String email);

}