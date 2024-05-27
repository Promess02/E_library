package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
}