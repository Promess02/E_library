package com.mikolaj.e_library.service;

import com.mikolaj.e_library.DTO.LoginForm;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.DTO.WorkerRegistrationForm;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReaderRepository readerRepository;
    @Mock
    private WorkerRepository workerRepository;
    @Mock
    private WarehouseManagerRepository warehouseManagerRepository;
    @Mock
    private EmployeeManagerRepository employeeManagerRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private User user;
    private WorkerRegistrationForm workerRegistrationForm;
    private LoginForm loginForm;
    private Reader reader;
    private Worker worker;
    private EmployeeManager employeeManager;
    private WarehouseManager warehouseManager;


    @BeforeEach
    void setUp() {
        user = new User("John", "Doe", "john.doe@example.com", "1234567890", "password");
        workerRegistrationForm = new WorkerRegistrationForm();
        workerRegistrationForm.setEmployerId(1);
        workerRegistrationForm.setUserId(1);
        workerRegistrationForm.setName("John");
        workerRegistrationForm.setSurname("Doe");
        workerRegistrationForm.setEmail("john.doe@example.com");
        workerRegistrationForm.setPhoneNumber("1234567890");
        workerRegistrationForm.setPassword("password");
        workerRegistrationForm.setMonthlyPay(5000);

        loginForm = new LoginForm();
        loginForm.setEmail("john.doe@example.com");
        loginForm.setPassword("password");
        reader = new Reader(user);
        worker = new Worker(5000, user, new EmployeeManager());
        employeeManager = new EmployeeManager(6000, user);
        warehouseManager = new WarehouseManager(5500, user, new EmployeeManager());
    }

    @Test
    void testRegisterReader_UserExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        ServiceResponse<Reader> response = registrationService.registerReader(user);

        assertTrue(response.getData().isEmpty());
        assertEquals("User with given email already exists!", response.getMessage());
    }

    @Test
    void testRegisterReader_Success() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        ServiceResponse<Reader> response = registrationService.registerReader(user);

        assertTrue(response.getData().isPresent());
        assertEquals("new reader registered", response.getMessage());
    }

    @Test
    void testRegisterWorker_EmployerNotExists() {
        when(employeeManagerRepository.existsById(workerRegistrationForm.getEmployerId())).thenReturn(false);

        ServiceResponse<Worker> response = registrationService.registerWorker(workerRegistrationForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Employer manager doesn't exist", response.getMessage());
    }

    @Test
    void testRegisterWorker_UserExists() {
        when(employeeManagerRepository.existsById(workerRegistrationForm.getEmployerId())).thenReturn(true);
        when(employeeManagerRepository.findById(workerRegistrationForm.getEmployerId())).thenReturn(Optional.of(new EmployeeManager()));
        when(userRepository.existsById(workerRegistrationForm.getUserId())).thenReturn(true);
        when(userRepository.findById(workerRegistrationForm.getUserId())).thenReturn(Optional.of(user));

        ServiceResponse<Worker> response = registrationService.registerWorker(workerRegistrationForm);

        assertTrue(response.getData().isPresent());
        assertEquals("Worker saved with existing user", response.getMessage());
    }

    @Test
    void testRegisterWorker_UserNotExists() {
        when(employeeManagerRepository.existsById(workerRegistrationForm.getEmployerId())).thenReturn(true);
        when(employeeManagerRepository.findById(workerRegistrationForm.getEmployerId())).thenReturn(Optional.of(new EmployeeManager()));
        when(userRepository.existsById(workerRegistrationForm.getUserId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ServiceResponse<Worker> response = registrationService.registerWorker(workerRegistrationForm);

        assertTrue(response.getData().isPresent());
        assertEquals("Worker saved with created user", response.getMessage());
    }

    @Test
    void testRegisterUser_UserExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        ServiceResponse<User> response = registrationService.registerUser(user);

        assertTrue(response.getData().isEmpty());
        assertEquals("User with given email already exists!", response.getMessage());
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        ServiceResponse<User> response = registrationService.registerUser(user);

        assertTrue(response.getData().isPresent());
        assertEquals("new reader registered", response.getMessage());
    }

    @Test
    void testRegisterWarehouseManager_EmployerNotExists() {
        when(employeeManagerRepository.existsById(workerRegistrationForm.getEmployerId())).thenReturn(false);

        ServiceResponse<WarehouseManager> response = registrationService.registerWarehouseManager(workerRegistrationForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Employer manager doesn't exist", response.getMessage());
    }

    @Test
    void testRegisterWarehouseManager_UserExists() {
        when(employeeManagerRepository.existsById(workerRegistrationForm.getEmployerId())).thenReturn(true);
        when(employeeManagerRepository.findById(workerRegistrationForm.getEmployerId())).thenReturn(Optional.of(new EmployeeManager()));
        when(userRepository.existsById(workerRegistrationForm.getUserId())).thenReturn(true);
        when(userRepository.findById(workerRegistrationForm.getUserId())).thenReturn(Optional.of(user));

        ServiceResponse<WarehouseManager> response = registrationService.registerWarehouseManager(workerRegistrationForm);

        assertTrue(response.getData().isPresent());
        assertEquals("Warehouse manager saved with existing user", response.getMessage());
    }

    @Test
    void testRegisterWarehouseManager_UserNotExists() {
        when(employeeManagerRepository.existsById(workerRegistrationForm.getEmployerId())).thenReturn(true);
        when(employeeManagerRepository.findById(workerRegistrationForm.getEmployerId())).thenReturn(Optional.of(new EmployeeManager()));
        when(userRepository.existsById(workerRegistrationForm.getUserId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ServiceResponse<WarehouseManager> response = registrationService.registerWarehouseManager(workerRegistrationForm);

        assertTrue(response.getData().isPresent());
        assertEquals("WarehouseManager saved with created user", response.getMessage());
    }

    @Test
    void testRegisterEmployeeManager_UserExists() {
        when(userRepository.existsById(workerRegistrationForm.getUserId())).thenReturn(true);
        when(userRepository.findById(workerRegistrationForm.getUserId())).thenReturn(Optional.of(user));

        ServiceResponse<EmployeeManager> response = registrationService.registerEmployeeManager(workerRegistrationForm);

        assertTrue(response.getData().isPresent());
        assertEquals("Employee manager saved with existing user", response.getMessage());
    }

    @Test
    void testRegisterEmployeeManager_UserNotExists() {
        when(userRepository.existsById(workerRegistrationForm.getUserId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ServiceResponse<EmployeeManager> response = registrationService.registerEmployeeManager(workerRegistrationForm);

        assertTrue(response.getData().isPresent());
        assertEquals("Employee Manager saved with created user", response.getMessage());
    }
    @Test
    void testLoginReader_ReaderDoesNotExist() {
        when(readerRepository.findByUserEmail(loginForm.getEmail())).thenReturn(Optional.empty());

        ServiceResponse<Reader> response = registrationService.loginReader(loginForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Reader doesn't exist", response.getMessage());
    }

    @Test
    void testLoginReader_WrongPassword() {
        when(readerRepository.findByUserEmail(loginForm.getEmail())).thenReturn(Optional.of(reader));
        loginForm.setPassword("wrongpassword");

        ServiceResponse<Reader> response = registrationService.loginReader(loginForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Wrong password", response.getMessage());
    }

    @Test
    void testLoginReader_Success() {
        when(readerRepository.findByUserEmail(loginForm.getEmail())).thenReturn(Optional.of(reader));

        ServiceResponse<Reader> response = registrationService.loginReader(loginForm);

        assertTrue(response.getData().isPresent());
        assertEquals("Reader logged in", response.getMessage());
    }

    @Test
    void testLoginWorker_WorkerDoesNotExist() {
        when(workerRepository.findByUserEmail(loginForm.getEmail())).thenReturn(Optional.empty());

        ServiceResponse<Worker> response = registrationService.loginWorker(loginForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Worker doesn't exist", response.getMessage());
    }

    @Test
    void testLoginWorker_WrongPassword() {
        when(workerRepository.findByUserEmail(loginForm.getEmail())).thenReturn(Optional.of(worker));
        loginForm.setPassword("wrongpassword");

        ServiceResponse<Worker> response = registrationService.loginWorker(loginForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Wrong password", response.getMessage());
    }

    @Test
    void testLoginWorker_Success() {
        when(workerRepository.findByUserEmail(loginForm.getEmail())).thenReturn(Optional.of(worker));

        ServiceResponse<Worker> response = registrationService.loginWorker(loginForm);

        assertTrue(response.getData().isPresent());
        assertEquals("Worker logged in", response.getMessage());
    }

    @Test
    void testLoginEmployeeManager_EmployeeManagerDoesNotExist() {
        when(employeeManagerRepository.findByUserEmail(loginForm.getEmail())).thenReturn(Optional.empty());

        ServiceResponse<EmployeeManager> response = registrationService.loginEmployeeManager(loginForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Employee Manager doesn't exist", response.getMessage());
    }

    @Test
    void testLoginEmployeeManager_WrongPassword() {
        when(employeeManagerRepository.findByUserEmail(loginForm.getEmail())).thenReturn(Optional.of(employeeManager));
        loginForm.setPassword("wrongpassword");

        ServiceResponse<EmployeeManager> response = registrationService.loginEmployeeManager(loginForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Wrong password", response.getMessage());
    }

    @Test
    void testLoginEmployeeManager_Success() {
        when(employeeManagerRepository.findByUserEmail(loginForm.getEmail())).thenReturn(Optional.of(employeeManager));

        ServiceResponse<EmployeeManager> response = registrationService.loginEmployeeManager(loginForm);

        assertTrue(response.getData().isPresent());
        assertEquals("Employee Manager logged in", response.getMessage());
    }

    @Test
    void testLoginWarehouseManager_WarehouseManagerDoesNotExist() {
        when(warehouseManagerRepository.findByUserEmail(loginForm.getEmail())).thenReturn(Optional.empty());

        ServiceResponse<WarehouseManager> response = registrationService.loginWarehouseManager(loginForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Warehouse Manager doesn't exist", response.getMessage());
    }

    @Test
    void testLoginWarehouseManager_WrongPassword() {
        when(warehouseManagerRepository.findByUserEmail(loginForm.getEmail())).thenReturn(Optional.of(warehouseManager));
        loginForm.setPassword("wrongpassword");

        ServiceResponse<WarehouseManager> response = registrationService.loginWarehouseManager(loginForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Wrong password", response.getMessage());
    }

    @Test
    void testLoginWarehouseManager_Success() {
        when(warehouseManagerRepository.findByUserEmail(loginForm.getEmail())).thenReturn(Optional.of(warehouseManager));

        ServiceResponse<WarehouseManager> response = registrationService.loginWarehouseManager(loginForm);

        assertTrue(response.getData().isPresent());
        assertEquals("Warehouse Manager logged in", response.getMessage());
    }
}