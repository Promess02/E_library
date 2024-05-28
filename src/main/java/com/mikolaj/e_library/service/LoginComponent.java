package com.mikolaj.e_library.service;

import com.mikolaj.e_library.repo.EmployeeManagerRepository;
import com.mikolaj.e_library.repo.ReaderRepository;
import com.mikolaj.e_library.repo.WarehouseManagerRepository;
import com.mikolaj.e_library.repo.WorkerRepository;
import org.springframework.stereotype.Component;

@Component
public class LoginComponent {
    private WarehouseManagerRepository warehouseManagerRepository;
    private EmployeeManagerRepository employeeManagerRepository;
    private WorkerRepository workerRepository;
    private ReaderRepository readerRepository;

    public LoginComponent(WarehouseManagerRepository warehouseManagerRepository,
                          EmployeeManagerRepository employeeManagerRepository,
                          WorkerRepository workerRepository, ReaderRepository readerRepository) {
        this.warehouseManagerRepository = warehouseManagerRepository;
        this.employeeManagerRepository = employeeManagerRepository;
        this.workerRepository = workerRepository;
        this.readerRepository = readerRepository;
    }

    public String checkUser(String email){
        if(readerRepository.existsByUserEmail(email)) return "reader";
        if(workerRepository.existsByUserEmail(email)) return "worker";
        if(warehouseManagerRepository.existsByUserEmail(email)) return "warehouse manager";
        if(employeeManagerRepository.existsByUserEmail(email)) return "employee manager";
        return "no registered user";
    }
}
