package com.mikolaj.e_library.service;

import com.mikolaj.e_library.DTO.LoginForm;
import com.mikolaj.e_library.DTO.ResetPassForm;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.DTO.WorkerRegistrationForm;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final ReaderRepository readerRepository;
    private final WorkerRepository workerRepository;
    private final WarehouseManagerRepository warehouseManagerRepository;
    private final EmployeeManagerRepository employeeManagerRepository;
    private final ApiKeyRepository apiKeyRepository;

    public RegistrationService(UserRepository userRepository, ReaderRepository readerRepository, WorkerRepository workerRepository,
                               WarehouseManagerRepository warehouseManagerRepository, EmployeeManagerRepository employeeManagerRepository, ApiKeyRepository apiKeyRepository) {
        this.userRepository = userRepository;
        this.readerRepository = readerRepository;
        this.workerRepository = workerRepository;
        this.warehouseManagerRepository = warehouseManagerRepository;
        this.employeeManagerRepository = employeeManagerRepository;
        this.apiKeyRepository = apiKeyRepository;
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
            ServiceResponse<Object> checkUser = checkIfExistsWithUserId(form.getUserId());
            if(!checkUser.getMessage().equals("ok")) return checkUser;
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
            ServiceResponse<Object> checkUser = checkIfExistsWithUserId(form.getUserId());
            if(!checkUser.getMessage().equals("ok")) return checkUser;
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

    public ServiceResponse<Object> registerEmployeeManager(WorkerRegistrationForm form) {
        if(userRepository.existsById(form.getUserId())){
            ServiceResponse<Object> check = checkIfExistsWithUserId(form.getUserId());
            if(!check.getMessage().equals("ok")) return check;
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

    public ServiceResponse<User> resetReaderPassword(ResetPassForm resetPassForm){
        Optional<User> user = userRepository.findByEmail(resetPassForm.getEmail());
        if(user.isEmpty()) return new ServiceResponse<>(Optional.empty(), "User with that email doesn't exist");
        if(user.get().getPassword().equals(resetPassForm.getNewPass())) return new ServiceResponse<>(Optional.empty(), "New password is the same as old one");
        user.get().setPassword(resetPassForm.getNewPass());
        userRepository.save(user.get());
        return new ServiceResponse<>(user, "Password reset");

    }

    private ServiceResponse<Object> checkGivenEmployer(int employerId){
        if(employerId==0) return new ServiceResponse<>(Optional.empty(), "Employer nor provided");
        if(!employeeManagerRepository.existsById(employerId)){
            return new ServiceResponse<>(Optional.empty(), "Employer manager doesn't exist");
        }

        return new ServiceResponse<>(Optional.empty(), "OK");
    }

    private ServiceResponse<Object> checkIfExistsWithUserId(int id){
        if(employeeManagerRepository.existsByUserId(id))
            return new ServiceResponse<>(Optional.empty(), "User with given id is an employee manager");
        if(workerRepository.existsByUserId(id))
            return new ServiceResponse<>(Optional.empty(), "User with given id is a worker");
        if(warehouseManagerRepository.existsByUserId(id))
            return new ServiceResponse<>(Optional.empty(), "User with given id is a warehouse Manager");
        return new ServiceResponse<>(Optional.empty(), "ok");
    }

        public String generateApiKey() {
            return UUID.randomUUID().toString();
        }

        @Transactional
        public void saveApiKey(int userId, String apiKey, String workerType) {
            ApiKey key = new ApiKey();
            key.setUserId(userId);
            key.setApiKey(apiKey);
            key.setUserType(workerType);
            int workerTypeId = 0;
            switch(workerType){
                case "worker" -> {
                    if(!workerRepository.existsByUserId(userId)) throw new RuntimeException("No worker for given id found");
                    workerTypeId = workerRepository.findByUserId(userId).get().getWorkerId();
                }
                case "warehouse manager" ->{
                    if(!warehouseManagerRepository.existsByUserId(userId)) throw new RuntimeException("No worker for given id found");
                    workerTypeId = warehouseManagerRepository.findByUserId(userId).get().getWareManId();
                }
                case "employee manager" -> {
                    if(!employeeManagerRepository.existsByUserId(userId)) throw new RuntimeException("No worker for given id found");
                    workerTypeId = employeeManagerRepository.findByUserId(userId).get().getEmpManId();
                }
                case "reader" -> {
                    if(!readerRepository.existsByUserId(userId)) throw new RuntimeException("No worker for given id found");
                    workerTypeId = readerRepository.findByUserId(userId).get().getReaderId();
                }
            }
            key.setWorkerTypeId(workerTypeId);
            apiKeyRepository.deleteAllByUserIdAndStatus(userId, "active");
            apiKeyRepository.save(key);
        }

        public int getWorkerTypeIdForApiKey(String key){
            Optional<ApiKey> apiKey = apiKeyRepository.findByApiKeyAndStatus(key, "active");
            return apiKey.map(ApiKey::getWorkerTypeId).orElse(-1);
        }

        public String getWorkerTypeForApiKey(String key){
            Optional<ApiKey> apiKey = apiKeyRepository.findByApiKeyAndStatus(key, "active");
            return apiKey.isPresent()?apiKey.get().getUserType():"user";
        }


        public String getUserEmailForApiKey(String key){
            ApiKey apiKey = apiKeyRepository.findByApiKeyAndStatus(key, "active").orElse(null);
            if(apiKey==null) return "";
            User user = userRepository.findById(apiKey.getUserId()).orElse(null);
            if(user==null) return "";
            return user.getEmail();
        }

        /*
            Do ustawienia sesji dla użytkownika na zatrzymaną. Ustawia status sesji na "terminated"
         */
        public void terminateSession(String apiKey){
            if(apiKey==null || apiKey.isEmpty()) return;
            Optional<ApiKey> apiKeyOptional =  apiKeyRepository.findByApiKey(apiKey);
            apiKeyOptional.ifPresent(key -> key.setStatus("terminated"));
            apiKeyRepository.save(apiKeyOptional.get());
        }

    public boolean handleAuthentication(String apiKey, List<String> userTypes){
        Optional<ApiKey> optionalApiKey = apiKeyRepository.findByApiKeyAndStatus(apiKey,"active");
        return !(optionalApiKey.isPresent() && userTypes.contains(optionalApiKey.get().getUserType()));
    }



}
