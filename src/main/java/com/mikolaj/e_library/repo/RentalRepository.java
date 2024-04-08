package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
}