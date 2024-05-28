package com.mikolaj.e_library.service;

import com.mikolaj.e_library.DTO.LoginForm;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.DTO.WorkerRegistrationForm;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.repo.*;
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

    public ServiceResponse<Object> registerWorker(WorkerRegistrationForm form) {
        ServiceResponse<Object> check = checkGivenEmployer(form.getEmployerId());
        if(!check.getMessage().equalsIgnoreCase("OK")) return check;
        EmployeeManager employeeManager = employeeManagerRepository.findById(form.getEmployerId()).orElse(null);
        if(userRepository.existsById(form.getUserId())){
            if(employeeManagerRepository.existsByUserId(form.getUserId()))
                return new ServiceResponse<>(Optional.empty(), "User with given id is an employee manager");
            if(warehouseManagerRepository.existsByUserId(form.getUserId()))
                return new ServiceResponse<>(Optional.empty(), "User with given id is a warehouse manager");
            User user = userRepository.findById(form.getUserId()).orElse(null);
            Worker worker = new Worker(form.getMonthlyPay(),
                    user, employeeManager, form.getPesel(), form.getPayAccountNumber(),form.getAddress());
            workerRepository.save(worker);
            return new ServiceResponse<>(Optional.of(worker), "Worker saved with existing user");
        }
        if(userRepository.existsByEmail(form.getEmail())) {
            return new ServiceResponse<>(Optional.empty(), "User with that email already exists");
        }
        User user = new User(form.getName(), form.getSurname(),
                form.getEmail(), form.getPhoneNumber(),
                form.getPassword());
        userRepository.save(user);
        Worker worker = new Worker(form.getMonthlyPay(), user, employeeManager,
                form.getPesel(), form.getPayAccountNumber(),form.getAddress());
        workerRepository.save(worker);
        return new ServiceResponse<>(Optional.of(worker), "Worker saved with created user");
    }

    public ServiceResponse<Object> registerWarehouseManager(WorkerRegistrationForm form) {
        ServiceResponse<Object> check = checkGivenEmployer(form.getEmployerId());
        if(!check.getMessage().equalsIgnoreCase("OK")) return check;
        EmployeeManager employeeManager = employeeManagerRepository.findById(form.getEmployerId()).orElse(null);
        if(userRepository.existsById(form.getUserId())){
            if(employeeManagerRepository.existsByUserId(form.getUserId()))
                return new ServiceResponse<>(Optional.empty(), "User with given id is an employee manager");
            if(workerRepository.existsByUserId(form.getUserId()))
                return new ServiceResponse<>(Optional.empty(), "User with given id is a worker");
            User user = userRepository.findById(form.getUserId()).orElse(null);
            WarehouseManager warehouseManager = new WarehouseManager(form.getMonthlyPay(),
                    user, employeeManager, form.getPesel(),form.getPayAccountNumber(),
                    form.getAddress());
            warehouseManagerRepository.save(warehouseManager);
            return new ServiceResponse<>(Optional.of(warehouseManager), "Warehouse manager saved with existing user");
        }
        if(userRepository.existsByEmail(form.getEmail()))
            return new ServiceResponse<>(Optional.empty(), "User with that email already exists");
        User user = new User(form.getName(), form.getSurname(),
                form.getEmail(), form.getPhoneNumber(),
                form.getPassword());
        userRepository.save(user);
        WarehouseManager warehouseManager = new WarehouseManager(form.getMonthlyPay(),
                user, employeeManager, form.getPesel(),form.getPayAccountNumber(),
                form.getAddress());
        warehouseManagerRepository.save(warehouseManager);
        return new ServiceResponse<>(Optional.of(warehouseManager), "WarehouseManager saved with created user");
    }

    public ServiceResponse<EmployeeManager> registerEmployeeManager(WorkerRegistrationForm form) {
        if(userRepository.existsById(form.getUserId())){
            if(warehouseManagerRepository.existsByUserId(form.getUserId()))
                return new ServiceResponse<>(Optional.empty(), "User with given id is a warehouse manager");
            if(workerRepository.existsByUserId(form.getUserId()))
                return new ServiceResponse<>(Optional.empty(), "User with given id is a worker");
            User user = userRepository.findById(form.getUserId()).orElse(null);
            EmployeeManager employeeManager = new EmployeeManager(form.getMonthlyPay()
                    ,user, form.getPesel(),
                    form.getPayAccountNumber(),
                    form.getAddress());
            employeeManagerRepository.save(employeeManager);
            return new ServiceResponse<>(Optional.of(employeeManager), "Employee manager saved with existing user");
        }
        if(userRepository.existsByEmail(form.getEmail()))
            return new ServiceResponse<>(Optional.empty(), "User with that email already exists");
        User user = new User(form.getName(), form.getSurname(),
                form.getEmail(), form.getPhoneNumber(),
                form.getPassword());
        userRepository.save(user);
        EmployeeManager employeeManager = new EmployeeManager(form.getMonthlyPay(),
                user, form.getPesel(),
                form.getPayAccountNumber(),
                form.getAddress());
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

    private ServiceResponse<Object> checkGivenEmployer(int employerId){
        if(employerId==0) return new ServiceResponse<>(Optional.empty(), "Employer nor provided");
        if(!employeeManagerRepository.existsById(employerId)){
            return new ServiceResponse<>(Optional.empty(), "Employer manager doesn't exist");
        }

        return new ServiceResponse<>(Optional.empty(), "OK");
    }

}
