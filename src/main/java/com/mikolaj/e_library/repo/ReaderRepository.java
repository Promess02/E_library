package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReaderRepository extends JpaRepository<Reader, Integer> {
    Optional<Reader> findByUserEmail(String email);
    boolean existsByUserId(int userId);
    Optional<Reader> findByUserId(int userId);
    boolean existsByUserEmail(String email);

    Page<Reader> findByUserEmailStartingWithIgnoreCase(String filter, Pageable pageable);
    Page<Reader> findByUserSurnameStartingWithIgnoreCase(String filter, Pageable pageable);

}