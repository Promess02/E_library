package com.mikolaj.e_library.controller;

import com.mikolaj.e_library.DTO.LoginForm;
import com.mikolaj.e_library.DTO.ResponseUtil;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.DTO.WorkerRegistrationForm;
import com.mikolaj.e_library.model.Reader;
import com.mikolaj.e_library.model.User;
import com.mikolaj.e_library.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/book")
@CrossOrigin(origins = "http://localhost:3000") // Add @CrossOrigin annotation to enable CORS for this method
public class UserController {
    private final RegistrationService registrationService;

    public UserController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /*Rejestruje czytelnika

        {
            "name": "Mikolaj"
		    "surname": "Michalczyk"
		    "email": "miko@wp.pl"
            "phoneNumber": "243512"
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
        "name": "Mikolaj"
        "surname": "Michalczyk"
        "email": "miko@wp.pl"
        "phoneNumber": "243512"
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

    {
        "workerType": "worker",
        "name": "mikolaj",
        "phoneNumber": "24421124",
        "email": "miko@wp.pl"
        "password": "nowe_haslo",
        "surname": "tajne_haslo"
        "userId": 4
        "employerId": 2
        "monthlyPay": 2345
    }
     */
    @PostMapping("/registerWorker")
    public ResponseEntity<?> registerWorker(@RequestBody WorkerRegistrationForm workerRegistrationForm){
        ServiceResponse<?> response;
        if(workerRegistrationForm.getWorkerType().equalsIgnoreCase("worker")){
            response = registrationService.registerWorker(workerRegistrationForm);
        } else if(workerRegistrationForm.getWorkerType().equalsIgnoreCase("warehouse Manager")){
            response = registrationService.registerWarehouseManager(workerRegistrationForm);
        } else if(workerRegistrationForm.getWorkerType().equalsIgnoreCase("employee Manager")){
            response = registrationService.registerEmployeeManager(workerRegistrationForm);
        } else {
            return ResponseUtil.badRequestResponse("incorrect worker type provided");
        }
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(),"Worker", response.getData());
    }

    /* Loguje wszystkie typy użytkowników.
    Podobnie do poprzedniego trzeba podać w polu "userType" jeden z następujących:
     worker, reader, warehouse Manager, employee Manager

        {
            "email": "miko@wp.pl",
            "password": "tajne_haslo",
            "userType": "worker"
        }
     */
    @GetMapping("/login")
    public ResponseEntity<?> loginUsers(@RequestBody LoginForm loginForm){
        ServiceResponse<?> response;
        if(loginForm.getUserType().equalsIgnoreCase("worker")){
            response = registrationService.loginWorker(loginForm);
        } else if(loginForm.getUserType().equalsIgnoreCase("warehouse Manager")){
            response = registrationService.loginWarehouseManager(loginForm);
        } else if(loginForm.getUserType().equalsIgnoreCase("employee Manager")){
            response = registrationService.loginEmployeeManager(loginForm);
        } else if(loginForm.getUserType().equalsIgnoreCase("reader")){
            response = registrationService.loginReader(loginForm);
        }else{
            return ResponseUtil.badRequestResponse("incorrect user type provided");
        }
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(),"User", response.getData());
    }


}
