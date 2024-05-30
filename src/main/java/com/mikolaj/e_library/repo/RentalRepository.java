package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.DTO.RentalStatus;
import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.Reader;
import com.mikolaj.e_library.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
    Optional<List<Rental>> findAllByReader(Reader reader);
    Optional<List<Rental>> findAllByReaderAndStatus(Reader reader, RentalStatus rentalStatus);
    List<Rental> findAllByReaderUserEmailAndStatus(String email, RentalStatus rentalStatus);
    List<Rental> findAllByReaderUserEmail(String email);
    Page<Rental> findByReaderUserEmail(String email, Pageable pageable);
    Page<Rental> findByReaderUserEmailAndStatus(String email, RentalStatus status, Pageable pageable);


}