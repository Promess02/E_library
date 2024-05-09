package com.mikolaj.e_library.service;

import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.DTO.WorkerRegistrationForm;
import com.mikolaj.e_library.model.EmployeeManager;
import com.mikolaj.e_library.model.Reader;
import com.mikolaj.e_library.model.User;
import com.mikolaj.e_library.model.Worker;
import com.mikolaj.e_library.repo.*;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final ReaderRepository readerRepository;
    private final WorkerRepository workerRepository;
    private final WarehouseManagerRepository warehouseManagerRepository;
    private final EmployeeManagerRepository employeeManagerRepository;

    public RegistrationService(UserRepository userRepository, ReaderRepository readerRepository, WorkerRepository workerRepository,
                               WarehouseManagerRepository warehouseManagerRepository, EmployeeManagerRepository employeeManagerRepository) {
        this.userRepository = userRepository;
        this.readerRepository = readerRepository;
        this.workerRepository = workerRepository;
        this.warehouseManagerRepository = warehouseManagerRepository;
        this.employeeManagerRepository = employeeManagerRepository;
    }

    public ServiceResponse<Reader> registerReader(User user) {
        if(userRepository.existsByEmail(user.getEmail())){
            return new ServiceResponse<>(Optional.empty(), "User with given email already exists!");
        }
        userRepository.save(user);

        Reader reader = new Reader(user);
        readerRepository.save(reader);

        return new ServiceResponse<>(Optional.of(reader), "new reader registered");
    }

    public ServiceResponse<User> registerUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())){
            return new ServiceResponse<>(Optional.empty(), "User with given email already exists!");
        }
        userRepository.save(user);

        return new ServiceResponse<>(Optional.of(user), "new reader registered");
    }

    public ServiceResponse<Worker> registerWorker(WorkerRegistrationForm workerRegistrationForm) {
        if(!employeeManagerRepository.existsById(workerRegistrationForm.getEmployerId())){
            return new ServiceResponse<>(Optional.empty(), "Employer manager doesn't exist");
        }
        EmployeeManager employeeManager = employeeManagerRepository.findById(workerRegistrationForm.getEmployerId()).orElse(null);
        if(userRepository.existsById(workerRegistrationForm.getUserId())){
            User user = userRepository.findById(workerRegistrationForm.getUserId()).orElse(null);
            Worker worker = new Worker(workerRegistrationForm.getMonthlyPay(),
                    user, employeeManager);
            workerRepository.save(worker);
            return new ServiceResponse<>(Optional.of(worker), "Worker saved with existing user");
        }
        User user = new User(workerRegistrationForm.getName(), workerRegistrationForm.getSurname(),
                workerRegistrationForm.getEmail(), workerRegistrationForm.getPhoneNumber(),
                workerRegistrationForm.getPassword());
        userRepository.save(user);
        Worker worker = new Worker(workerRegistrationForm.getMonthlyPay(), user, employeeManager);
        workerRepository.save(worker);
        return new ServiceResponse<>(Optional.of(worker), "Worker saved with created user");
    }


}
