package com.mikolaj.e_library.service;

import com.mikolaj.e_library.DTO.LoginForm;
import com.mikolaj.e_library.DTO.ResetPassForm;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.DTO.WorkerRegistrationForm;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Mock
    private ApiKeyRepository apiKeyRepository;

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
        workerRegistrationForm.setWorkerType("worker");
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

        ServiceResponse<Object> response = registrationService.registerWorker(workerRegistrationForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Employer manager doesn't exist", response.getMessage());
    }

    @Test
    void testRegisterWorker_UserExists() {
        when(employeeManagerRepository.existsById(workerRegistrationForm.getEmployerId())).thenReturn(true);
        when(employeeManagerRepository.findById(workerRegistrationForm.getEmployerId())).thenReturn(Optional.of(employeeManager));
        when(userRepository.existsById(workerRegistrationForm.getUserId())).thenReturn(true);
        when(userRepository.findById(workerRegistrationForm.getUserId())).thenReturn(Optional.of(user));

        ServiceResponse<Object> response = registrationService.registerWorker(workerRegistrationForm);

        assertTrue(response.getData().isPresent());
        assertEquals("Worker saved with existing user", response.getMessage());
    }

    @Test
    void testRegisterWorker_UserNotExists() {
        when(employeeManagerRepository.existsById(workerRegistrationForm.getEmployerId())).thenReturn(true);
        when(employeeManagerRepository.findById(workerRegistrationForm.getEmployerId())).thenReturn(Optional.of(new EmployeeManager()));
        when(userRepository.existsById(workerRegistrationForm.getUserId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ServiceResponse<Object> response = registrationService.registerWorker(workerRegistrationForm);

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

        ServiceResponse<Object> response = registrationService.registerWarehouseManager(workerRegistrationForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Employer manager doesn't exist", response.getMessage());
    }

    @Test
    void testRegisterWarehouseManager_UserExists() {
        when(employeeManagerRepository.existsById(workerRegistrationForm.getEmployerId())).thenReturn(true);
        when(employeeManagerRepository.findById(workerRegistrationForm.getEmployerId())).thenReturn(Optional.of(employeeManager));
        when(userRepository.existsById(workerRegistrationForm.getUserId())).thenReturn(true);
        when(userRepository.findById(workerRegistrationForm.getUserId())).thenReturn(Optional.of(user));

        ServiceResponse<Object> response = registrationService.registerWarehouseManager(workerRegistrationForm);

        assertTrue(response.getData().isPresent());
        assertEquals("Warehouse manager saved with existing user", response.getMessage());
    }

    @Test
    void testRegisterWarehouseManager_UserNotExists() {
        when(employeeManagerRepository.existsById(workerRegistrationForm.getEmployerId())).thenReturn(true);
        when(employeeManagerRepository.findById(workerRegistrationForm.getEmployerId())).thenReturn(Optional.of(new EmployeeManager()));
        when(userRepository.existsById(workerRegistrationForm.getUserId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ServiceResponse<Object> response = registrationService.registerWarehouseManager(workerRegistrationForm);

        assertTrue(response.getData().isPresent());
        assertEquals("WarehouseManager saved with created user", response.getMessage());
    }

    @Test
    void testRegisterEmployeeManager_UserExists() {
        when(userRepository.existsById(workerRegistrationForm.getUserId())).thenReturn(true);
        when(userRepository.findById(workerRegistrationForm.getUserId())).thenReturn(Optional.of(user));

        ServiceResponse<Object> response = registrationService.registerEmployeeManager(workerRegistrationForm);

        assertTrue(response.getData().isPresent());
        assertEquals("Employee manager saved with existing user", response.getMessage());
    }

    @Test
    void testRegisterEmployeeManager_UserNotExists() {
        when(userRepository.existsById(workerRegistrationForm.getUserId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ServiceResponse<Object> response = registrationService.registerEmployeeManager(workerRegistrationForm);

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

    @Test
    public void test_deals_with_invalid_worker_type_values() {
        int userId = 1;
        String apiKey = "new-api-key";

        // Test with invalid worker type
        registrationService.saveApiKey(userId, apiKey, "");

        verify(apiKeyRepository).save(any(ApiKey.class));
    }


        // Valid API key with matching user type returns false
        @Test
        public void test_valid_api_key_with_matching_user_type_returns_false() {

            String apiKey = "validApiKey";
            List<String> userTypes = List.of("userType1");
            ApiKey apiKeyEntity = new ApiKey();
            apiKeyEntity.setApiKey(apiKey);
            apiKeyEntity.setUserType("userType1");
            when(apiKeyRepository.findByApiKeyAndStatus(apiKey, "active")).thenReturn(Optional.of(apiKeyEntity));
            boolean result = registrationService.handleAuthentication(apiKey, userTypes);
            assertFalse(result);
        }

        // Valid API key with multiple matching user types returns false
        @Test
        public void test_valid_api_key_with_multiple_matching_user_types_returns_false() {
            String apiKey = "validApiKey";
            List<String> userTypes = List.of("userType1", "userType2");
            ApiKey apiKeyEntity = new ApiKey();
            apiKeyEntity.setApiKey(apiKey);
            apiKeyEntity.setUserType("userType1");
            when(apiKeyRepository.findByApiKeyAndStatus(apiKey, "active")).thenReturn(Optional.of(apiKeyEntity));
            boolean result = registrationService.handleAuthentication(apiKey, userTypes);
            assertFalse(result);
        }

        // Valid API key with non-matching user type returns true
        @Test
        public void test_valid_api_key_with_non_matching_user_type_returns_true() {
            String apiKey = "validApiKey";
            List<String> userTypes = List.of("userType2");
            ApiKey apiKeyEntity = new ApiKey();
            apiKeyEntity.setApiKey(apiKey);
            apiKeyEntity.setUserType("userType1");
            when(apiKeyRepository.findByApiKeyAndStatus(apiKey, "active")).thenReturn(Optional.of(apiKeyEntity));
            boolean result = registrationService.handleAuthentication(apiKey, userTypes);
            assertTrue(result);
        }

        // Valid API key with empty user types list returns true
        @Test
        public void test_valid_api_key_with_empty_user_types_list_returns_true() {
            String apiKey = "validApiKey";
            List<String> userTypes = List.of();
            ApiKey apiKeyEntity = new ApiKey();
            apiKeyEntity.setApiKey(apiKey);
            apiKeyEntity.setUserType("userType1");
            when(apiKeyRepository.findByApiKeyAndStatus(apiKey, "active")).thenReturn(Optional.of(apiKeyEntity));
            boolean result = registrationService.handleAuthentication(apiKey, userTypes);
            assertTrue(result);
        }

        // Inactive API key returns true
        @Test
        public void test_inactive_api_key_returns_true() {
            String apiKey = "inactiveApiKey";
            List<String> userTypes = List.of("userType1");
            when(apiKeyRepository.findByApiKeyAndStatus(apiKey, "active")).thenReturn(Optional.empty());
            boolean result = registrationService.handleAuthentication(apiKey, userTypes);
            assertTrue(result);
        }

        // Null API key returns true
        @Test
        public void test_null_api_key_returns_true() {
            String apiKey = null;
            List<String> userTypes = List.of("userType1");
            boolean result = registrationService.handleAuthentication(apiKey, userTypes);
            assertTrue(result);
        }

        // Empty string API key returns true
        @Test
        public void test_empty_string_api_key_returns_true() {
            String apiKey = "";
            List<String> userTypes = List.of("userType1");
            boolean result = registrationService.handleAuthentication(apiKey, userTypes);
            assertTrue(result);
        }

        // API key with special characters returns true if not found
        @Test
        public void test_api_key_with_special_characters_returns_true_if_not_found() {
            String apiKey = "!@#$%^&*()";
            List<String> userTypes = List.of("userType1");
            when(apiKeyRepository.findByApiKeyAndStatus(apiKey, "active")).thenReturn(Optional.empty());
            boolean result = registrationService.handleAuthentication(apiKey, userTypes);
            assertTrue(result);
        }

        // API key with mixed case sensitivity returns true if not found
        @Test
        public void test_api_key_with_mixed_case_sensitivity_returns_true_if_not_found() {
            String apiKey = "ValidApiKEY";
            List<String> userTypes = List.of("userType1");
            when(apiKeyRepository.findByApiKeyAndStatus(apiKey.toLowerCase(), "active")).thenReturn(Optional.empty());
            boolean result = registrationService.handleAuthentication(apiKey.toLowerCase(), userTypes);
            assertTrue(result);
        }

        // User type list with null values returns true
        @Test
        public void test_user_type_list_with_null_values_returns_true() {
            String apiKey = "validApiKey";
            List<String> userTypes = Arrays.asList((String) null);
            ApiKey apiKeyEntity = new ApiKey();
            apiKeyEntity.setApiKey(apiKey);
            apiKeyEntity.setUserType("userType1");
            when(apiKeyRepository.findByApiKeyAndStatus(apiKey, "active")).thenReturn(Optional.of(apiKeyEntity));
            boolean result = registrationService.handleAuthentication(apiKey, userTypes);
            assertTrue(result);
        }

        // Successfully terminates an active session by setting status to "terminated"
        @Test
        public void test_terminate_session_success() {
            // Generate a random API key for testing
            String apiKey = registrationService.generateApiKey();
            ApiKey apiKey1 = new ApiKey(1,apiKey,1,"ACTIVE","worker",1);

            when(apiKeyRepository.findByApiKey(apiKey)).thenReturn(Optional.of(apiKey1));

            // Call the method to terminate the session
            registrationService.terminateSession(apiKey);

            // Retrieve the API key after termination
            Optional<ApiKey> terminatedApiKey = apiKeyRepository.findByApiKey(apiKey);

            // Check if the status is set to "terminated"
            assertTrue(terminatedApiKey.isPresent());
            assertEquals("terminated", terminatedApiKey.get().getStatus());
        }

        // Saves the updated ApiKey object with status "terminated" in the repository
        @Test
        public void test_terminate_session_updates_apikey_status() {
            // Create a sample ApiKey object
            ApiKey apiKey = new ApiKey();
            apiKey.setApiKey("sample_api_key");
            apiKey.setUserId(1); // Assuming user ID is 1
            apiKey.setStatus("active"); // Initial status

            // Mock the repository method to return the ApiKey object
            when(apiKeyRepository.findByApiKey(apiKey.getApiKey())).thenReturn(Optional.of(apiKey));

            // Call the service method
            registrationService.terminateSession(apiKey.getApiKey());

            // Verify that the repository method was called with the correct argument
            verify(apiKeyRepository).save(apiKey);

            // Check if the status is updated to "terminated"
            assertEquals("terminated", apiKey.getStatus());
        }

        // Provided apiKey is null or empty
        @Test
        public void test_terminate_session_null_or_empty_apiKey() {
            registrationService.terminateSession(null);
            verify(apiKeyRepository, never()).findByApiKey(any());

            // Test when apiKey is empty
            registrationService.terminateSession("");
            verify(apiKeyRepository, never()).findByApiKey(any());
        }

    @Test
    public void testResetReaderPassword_UserNotFound() {
        ResetPassForm form = new ResetPassForm("nonexistent@example.com", "newPass");

        when(userRepository.findByEmail(form.getEmail())).thenReturn(Optional.empty());

        ServiceResponse<User> response = registrationService.resetReaderPassword(form);

        assertTrue(response.getData().isEmpty());
        assertEquals("User with that email doesn't exist", response.getMessage());
    }

    @Test
    public void testResetReaderPassword_PasswordSameAsOld() {
        User user = new User();
        user.setPassword("oldPass");
        ResetPassForm form = new ResetPassForm("user@example.com", "oldPass");

        when(userRepository.findByEmail(form.getEmail())).thenReturn(Optional.of(user));

        ServiceResponse<User> response = registrationService.resetReaderPassword(form);

        assertTrue(response.getData().isEmpty());
        assertEquals("New password is the same as old one", response.getMessage());
    }

    @Test
    public void testResetReaderPassword_Success() {
        User user = new User();
        user.setPassword("oldPass");
        ResetPassForm form = new ResetPassForm("oldPass", "newPass", "email");

        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));

        ServiceResponse<User> response = registrationService.resetReaderPassword(form);

        assertTrue(response.getData().isPresent());
        assertEquals("Password reset", response.getMessage());
        assertEquals("newPass", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    public void testGetWorkerTypeIdForApiKey_ApiKeyNotFound() {
        when(apiKeyRepository.findByApiKeyAndStatus(anyString(), eq("active"))).thenReturn(Optional.empty());

        int workerTypeId = registrationService.getWorkerTypeIdForApiKey("someKey");

        assertEquals(-1, workerTypeId);
    }

    @Test
    public void testGetWorkerTypeIdForApiKey_Success() {
        ApiKey apiKey = new ApiKey();
        apiKey.setWorkerTypeId(123);
        when(apiKeyRepository.findByApiKeyAndStatus(anyString(), eq("active"))).thenReturn(Optional.of(apiKey));

        int workerTypeId = registrationService.getWorkerTypeIdForApiKey("someKey");

        assertEquals(123, workerTypeId);
    }

    @Test
    public void testGetWorkerTypeForApiKey_ApiKeyNotFound() {
        when(apiKeyRepository.findByApiKeyAndStatus(anyString(), eq("active"))).thenReturn(Optional.empty());

        String workerType = registrationService.getWorkerTypeForApiKey("someKey");

        assertEquals("user", workerType);
    }

    @Test
    public void testGetWorkerTypeForApiKey_Success() {
        ApiKey apiKey = new ApiKey();
        apiKey.setUserType("worker");
        when(apiKeyRepository.findByApiKeyAndStatus(anyString(), eq("active"))).thenReturn(Optional.of(apiKey));

        String workerType = registrationService.getWorkerTypeForApiKey("someKey");

        assertEquals("worker", workerType);
    }

    @Test
    public void testGetUserEmailForApiKey_ApiKeyNotFound() {
        when(apiKeyRepository.findByApiKeyAndStatus(anyString(), eq("active"))).thenReturn(Optional.empty());

        String email = registrationService.getUserEmailForApiKey("someKey");

        assertEquals("", email);
    }

    @Test
    public void testGetUserEmailForApiKey_UserNotFound() {
        ApiKey apiKey = new ApiKey();
        apiKey.setUserId(1);
        when(apiKeyRepository.findByApiKeyAndStatus(anyString(), eq("active"))).thenReturn(Optional.of(apiKey));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        String email = registrationService.getUserEmailForApiKey("someKey");

        assertEquals("", email);
    }

    @Test
    public void testGetUserEmailForApiKey_Success() {
        ApiKey apiKey = new ApiKey();
        apiKey.setUserId(1);
        User user = new User();
        user.setEmail("user@example.com");

        when(apiKeyRepository.findByApiKeyAndStatus(anyString(), eq("active"))).thenReturn(Optional.of(apiKey));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        String email = registrationService.getUserEmailForApiKey("someKey");

        assertEquals("user@example.com", email);
    }

    @Test
    public void testSaveApiKey_NoWorkerFound() {
        int userId = 1;
        String apiKey = "someApiKey";

        when(workerRepository.existsByUserId(anyInt())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            registrationService.saveApiKey(userId, apiKey, "worker");
        });

        assertEquals("No worker for given id found", exception.getMessage());
    }

    @Test
    public void testSaveApiKey_Success() {
        int userId = 1;
        String apiKey = "someApiKey";
        Worker worker = new Worker();
        worker.setWorkerId(10);

        when(workerRepository.existsByUserId(anyInt())).thenReturn(true);
        when(workerRepository.findByUserId(anyInt())).thenReturn(Optional.of(worker));

        registrationService.saveApiKey(userId, apiKey, "worker");

        verify(apiKeyRepository).deleteAllByUserIdAndStatus(userId, "active");
        verify(apiKeyRepository).save(any(ApiKey.class));
    }



}