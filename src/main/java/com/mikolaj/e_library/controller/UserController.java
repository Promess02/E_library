package com.mikolaj.e_library.controller;

import com.mikolaj.e_library.DTO.LoginForm;
import com.mikolaj.e_library.DTO.ResponseUtil;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.DTO.WorkerRegistrationForm;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.service.LoginComponent;
import com.mikolaj.e_library.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000") // Add @CrossOrigin annotation to enable CORS for this method
public class UserController {
    private final RegistrationService registrationService;
    private final LoginComponent loginComponent;

    public UserController(RegistrationService registrationService, LoginComponent loginComponent) {
        this.registrationService = registrationService;
        this.loginComponent = loginComponent;
    }

    /*Rejestruje czytelnika

        {
            "name": "Mikolaj",
		    "surname": "Michalczyk",
		    "email": "miko@wp.pl",
            "phoneNumber": "243512",
            "password": "tajne_haslo"
        }
     */
    @PostMapping("/registerReader")
    public ResponseEntity<?> registerReader(@RequestBody User user){
        ServiceResponse<Reader> response = registrationService.registerReader(user);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Reader", response.getData());
    }

    /*Rejestruje uzytkownika

    {
        "name": "Mikolaj",
        "surname": "Michalczyk",
        "email": "miko@wp.pl",
        "phoneNumber": "243512",
        "password": "tajne_haslo"
    }
 */
    @PostMapping("/registerUser")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        ServiceResponse<User> response = registrationService.registerUser(user);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "User", response.getData());
    }
    /*
    Rejestruje wszystkie możliwe typy pracowników. Oprócz danych użytkownika można podać userId,
    aby stworzyć profil pracownika dla istniejącego użytkownia. Potrzebne też jest podanie jednego z typów użytkownika
    podając jeden z następujących stringów: worker, warehouse manager, employee manager. W przeciwnym wypadku
    zwróci odpowiedź badRequest. Można stworzyć pracownika poprzez podanie nowych informacji albo id istniejącego
    użytkownika. Żądanie:

    1 sposób - wszystkie dane
    {
        "workerType": "worker",
        "name": "mikolaj",
        "phoneNumber": "24421124",
        "email": "miko@wp.pl",
        "password": "nowe_haslo",
        "surname": "tajne_haslo",
        "pesel": "121415",
        "payAccountNumber": "241441421",
        "address": "Zakatek 12",
        "employerId": 2,
        "monthlyPay": 2345
    }
    2 sposób - rejestracja przez istniejącego użytkownika
        {
        "workerType": "worker",
        "userId": 1,
        "pesel": "121415",
        "payAccountNumber": "241441421",
        "address": "Zakatek 12",
        "employerId": 2,
        "monthlyPay": 2345
    }
     */
    @PostMapping("/registerWorker/apiKey={apiKey}")
    public ResponseEntity<?> registerWorker(@RequestBody WorkerRegistrationForm workerRegistrationForm, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("employee manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<?> response;
        if(workerRegistrationForm.getWorkerType().equalsIgnoreCase("worker")){
            response = registrationService.registerWorker(workerRegistrationForm);
        } else if(workerRegistrationForm.getWorkerType().equalsIgnoreCase("warehouse manager")){
            response = registrationService.registerWarehouseManager(workerRegistrationForm);
        } else if(workerRegistrationForm.getWorkerType().equalsIgnoreCase("employee manager")){
            response = registrationService.registerEmployeeManager(workerRegistrationForm);
        } else {
            return ResponseUtil.badRequestResponse("incorrect worker type provided");
        }
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(),"Worker", response.getData());
    }

    /* Loguje wszystkie typy użytkowników.

        {
            "email": "miko@wp.pl",
            "password": "tajne_haslo",
        }

        Zwraca teraz dane użytkownika pod "user", typ użytkownika pod "userType": worker, reader, employee manager, warehouse manager
        zwraca klucz api pod "apiKey".

     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUsers(@RequestBody LoginForm loginForm){
        ServiceResponse<?> response;
        String userType = loginComponent.checkUser(loginForm.getEmail());
        int generatedId;

        switch (userType){
            case "worker" -> {
                response = registrationService.loginWorker(loginForm);
                Worker worker = (Worker) response.getData().get();
                generatedId = worker.getUser().getId();
            }
            case "reader" ->{
                response = registrationService.loginReader(loginForm);
                Reader reader = (Reader) response.getData().get();
                generatedId = reader.getUser().getId();
            }
            case "employee manager" -> {
                response = registrationService.loginEmployeeManager(loginForm);
                EmployeeManager manager = (EmployeeManager) response.getData().get();
                generatedId = manager.getUser().getId();
            }
            case "warehouse manager" -> {
                response = registrationService.loginWarehouseManager(loginForm);
                WarehouseManager warehouseManager = (WarehouseManager) response.getData().get();
                generatedId = warehouseManager.getUser().getId();
            }
            default -> {
                return ResponseUtil.badRequestResponse("No user with email registered");
            }
        }

        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());

        String apiKey = registrationService.generateApiKey();
        registrationService.saveApiKey(generatedId, apiKey, userType);

        // Return response with API key
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("user", response.getData());
        responseData.put("userType", userType);
        responseData.put("apiKey", apiKey);

        return ResponseUtil.okResponse(response.getMessage(), "User", responseData);
    }
}
