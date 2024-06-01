package com.mikolaj.e_library.controller;

import com.mikolaj.e_library.DTO.PayRiseForm;
import com.mikolaj.e_library.DTO.ResponseUtil;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.model.EmployeeManager;
import com.mikolaj.e_library.model.WarehouseManager;
import com.mikolaj.e_library.model.Worker;
import com.mikolaj.e_library.service.EmployeeManagerService;
import com.mikolaj.e_library.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employeeManager")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeManagerController {
    private EmployeeManagerService employeeManagerService;
    private RegistrationService registrationService;

    public EmployeeManagerController(EmployeeManagerService employeeManagerService, RegistrationService registrationService) {
        this.employeeManagerService = employeeManagerService;
        this.registrationService = registrationService;
    }

    @GetMapping("/getAllWorkers/apiKey={apiKey}")
    public ResponseEntity<?> getAllWorkers(@PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("employee manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<List<Worker>> response = employeeManagerService.getAllWorkers();
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Workers", response.getData());
    }

    @GetMapping("/getAllWarehouseManagers/apiKey={apiKey}")
    public ResponseEntity<?> getAllWarehouseManagers(@PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("employee manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<List<WarehouseManager>> response = employeeManagerService.getAllWarehouseManagers();
        if (response.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(response.getMessage());
        }
        return ResponseUtil.okResponse(response.getMessage(), "WarehouseManagers", response.getData().get());
    }

    /*
        {
            "workerId": 1,
            "pesel": "1241421",
            "payAccountNumber": "3264151",
            "address": "Kochanów Wieniawski 23a"
        }
     */
    @PatchMapping("/updateWorker/apiKey={apiKey}")
    public ResponseEntity<?> updateWorker(@RequestBody Worker worker, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("employee manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<Worker> response = employeeManagerService.updateWorker(worker);
        if (response.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(response.getMessage());
        }
        return ResponseUtil.okResponse(response.getMessage(), "Worker", response.getData().get());
    }

    /*
    {
        "wareManId": 1,
        "pesel": "1241421",
        "payAccountNumber": "3264151",
        "address": "Kochanów Wieniawski 23a"
    }
 */
    @PatchMapping("/updateWarehouseManager/apiKey={apiKey}")
    public ResponseEntity<?> updateWarehouseManager(@RequestBody WarehouseManager warehouseManager, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("employee manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<WarehouseManager> response = employeeManagerService.updateWarehouseManager(warehouseManager);
        if (response.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(response.getMessage());
        }
        return ResponseUtil.okResponse(response.getMessage(), "WarehouseManager", response.getData().get());
    }
    /*
    {
        "workerId": 2
    }
    */
    @DeleteMapping("/deleteWorker/apiKey={apiKey}")
    public ResponseEntity<?> deleteWorker(@RequestBody Worker worker, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("employee manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<Worker> response = employeeManagerService.deleteWorker(worker);
        if (response.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(response.getMessage());
        }
        return ResponseUtil.okResponse(response.getMessage(), "Worker", response.getData().get());
    }
/*
    {
        "wareManId":2
    }
 */
    @DeleteMapping("/deleteWarehouseManager/apiKey={apiKey}")
    public ResponseEntity<?> deleteWarehouseManager(@RequestBody WarehouseManager warehouseManager, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("employee manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<WarehouseManager> response = employeeManagerService.deleteWarehouseManager(warehouseManager);
        if (response.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(response.getMessage());
        }
        return ResponseUtil.okResponse(response.getMessage(), "WarehouseManager", response.getData().get());
    }
/*
{
    "workerId": 1,
    "monthlyPay": 5000
}
 */
    @PatchMapping("/changeWorkerMonthlyPay/apiKey={apiKey}")
    public ResponseEntity<?> changeWorkerMonthlyPay(@RequestBody PayRiseForm payRiseForm, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("employee manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<Worker> response = employeeManagerService.changeWorkerMonthlyPay(payRiseForm);
        if (response.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(response.getMessage());
        }
        return ResponseUtil.okResponse(response.getMessage(), "Worker", response.getData().get());
    }

    /*
    {
        "workerId": 1,
            "monthlyPay": 5000
    }
     */
    @PatchMapping("/changeWarehouseManagerMonthlyPay/apiKey={apiKey}")
    public ResponseEntity<?> changeWarehouseManagerMonthlyPay(@RequestBody PayRiseForm payRiseForm, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("employee manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<WarehouseManager> response = employeeManagerService.changeWarehouseManagerMonthlyPay(payRiseForm);
        if (response.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(response.getMessage());
        }
        return ResponseUtil.okResponse(response.getMessage(), "WarehouseManager", response.getData().get());
    }
}
