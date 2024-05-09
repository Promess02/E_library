package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.DTO.RentalStatus;
import com.mikolaj.e_library.model.Reader;
import com.mikolaj.e_library.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
    public Optional<List<Rental>> findAllByReader(Reader reader);
    public Optional<List<Rental>> findAllByReaderAndStatus(Reader reader, RentalStatus rentalStatus);

}