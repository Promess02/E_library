package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.DTO.RentalStatus;
import com.mikolaj.e_library.model.Reader;
import com.mikolaj.e_library.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
    Optional<List<Rental>> findAllByReader(Reader reader);
    Optional<List<Rental>> findAllByReaderAndStatus(Reader reader, RentalStatus rentalStatus);
    List<Rental> findAllByReaderUserEmailAndStatus(String email, RentalStatus rentalStatus);
    List<Rental> findAllByReaderUserEmail(String email);


}