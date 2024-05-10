package com.mikolaj.e_library.service;

import com.mikolaj.e_library.DTO.LoginForm;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.DTO.WorkerRegistrationForm;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.repo.*;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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

    public ServiceResponse<WarehouseManager> registerWarehouseManager(WorkerRegistrationForm workerRegistrationForm) {
        if(!employeeManagerRepository.existsById(workerRegistrationForm.getEmployerId())){
            return new ServiceResponse<>(Optional.empty(), "Employer manager doesn't exist");
        }
        EmployeeManager employeeManager = employeeManagerRepository.findById(workerRegistrationForm.getEmployerId()).orElse(null);
        if(userRepository.existsById(workerRegistrationForm.getUserId())){
            User user = userRepository.findById(workerRegistrationForm.getUserId()).orElse(null);
            WarehouseManager warehouseManager = new WarehouseManager(workerRegistrationForm.getMonthlyPay(),
                    user, employeeManager);
            warehouseManagerRepository.save(warehouseManager);
            return new ServiceResponse<>(Optional.of(warehouseManager), "Warehouse manager saved with existing user");
        }
        User user = new User(workerRegistrationForm.getName(), workerRegistrationForm.getSurname(),
                workerRegistrationForm.getEmail(), workerRegistrationForm.getPhoneNumber(),
                workerRegistrationForm.getPassword());
        userRepository.save(user);
        WarehouseManager warehouseManager = new WarehouseManager(workerRegistrationForm.getMonthlyPay(),
                user, employeeManager);
        warehouseManagerRepository.save(warehouseManager);
        return new ServiceResponse<>(Optional.of(warehouseManager), "WarehouseManager saved with created user");
    }

    public ServiceResponse<EmployeeManager> registerEmployeeManager(WorkerRegistrationForm workerRegistrationForm) {
        if(userRepository.existsById(workerRegistrationForm.getUserId())){
            User user = userRepository.findById(workerRegistrationForm.getUserId()).orElse(null);
            EmployeeManager employeeManager = new EmployeeManager(workerRegistrationForm.getMonthlyPay(),
                    user);
            employeeManagerRepository.save(employeeManager);
            return new ServiceResponse<>(Optional.of(employeeManager), "Employee manager saved with existing user");
        }
        User user = new User(workerRegistrationForm.getName(), workerRegistrationForm.getSurname(),
                workerRegistrationForm.getEmail(), workerRegistrationForm.getPhoneNumber(),
                workerRegistrationForm.getPassword());
        userRepository.save(user);
        EmployeeManager employeeManager = new EmployeeManager(workerRegistrationForm.getMonthlyPay(),
                user);
        employeeManagerRepository.save(employeeManager);
        return new ServiceResponse<>(Optional.of(employeeManager), "Employee Manager saved with created user");
    }

    public ServiceResponse<Reader> loginReader(LoginForm loginForm){
        Optional<Reader> reader = readerRepository.findByUserEmail(loginForm.getEmail());
        if(reader.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Reader doesn't exist");
        if(!loginForm.getPassword().equals(reader.get().getUser().getPassword()))
            return new ServiceResponse<>(Optional.empty(), "Wrong password");
        return new ServiceResponse<>(reader, "Reader logged in");
    }

    public ServiceResponse<Worker> loginWorker(LoginForm loginForm){
        Optional<Worker> worker = workerRepository.findByUserEmail(loginForm.getEmail());
        if(worker.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Worker doesn't exist");
        if(!loginForm.getPassword().equals(worker.get().getUser().getPassword()))
            return new ServiceResponse<>(Optional.empty(), "Wrong password");
        return new ServiceResponse<>(worker, "Worker logged in");
    }

    public ServiceResponse<EmployeeManager> loginEmployeeManager(LoginForm loginForm){
        Optional<EmployeeManager> empManager = employeeManagerRepository.findByUserEmail(loginForm.getEmail());
        if(empManager.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Employee Manager doesn't exist");
        if(!loginForm.getPassword().equals(empManager.get().getUser().getPassword()))
            return new ServiceResponse<>(Optional.empty(), "Wrong password");
        return new ServiceResponse<>(empManager, "Employee Manager logged in");
    }

    public ServiceResponse<WarehouseManager> loginWarehouseManager(LoginForm loginForm){
        Optional<WarehouseManager> warehouseManager = warehouseManagerRepository.findByUserEmail(loginForm.getEmail());
        if(warehouseManager.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Warehouse Manager doesn't exist");
        if(!loginForm.getPassword().equals(warehouseManager.get().getUser().getPassword()))
            return new ServiceResponse<>(Optional.empty(), "Wrong password");
        return new ServiceResponse<>(warehouseManager, "Warehouse Manager logged in");
    }

}
