package com.mikolaj.e_library.controller;

import com.mikolaj.e_library.DTO.PayRiseForm;
import com.mikolaj.e_library.DTO.ResponseUtil;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.model.EmployeeManager;
import com.mikolaj.e_library.model.WarehouseManager;
import com.mikolaj.e_library.model.Worker;
import com.mikolaj.e_library.service.EmployeeManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employeeManager")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeManagerController {
    private EmployeeManagerService employeeManagerService;

    public EmployeeManagerController(EmployeeManagerService employeeManagerService) {
        this.employeeManagerService = employeeManagerService;
    }

    @GetMapping("/getAllWorkers")
    public ResponseEntity<?> getAllWorkers(){
        ServiceResponse<List<Worker>> response = employeeManagerService.getAllWorkers();
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Workers", response.getData());
    }

    @GetMapping("/getAllWarehouseManagers")
    public ResponseEntity<?> getAllWarehouseManagers() {
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
    @PatchMapping("/updateWorker")
    public ResponseEntity<?> updateWorker(@RequestBody Worker worker) {
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
    @PatchMapping("/updateWarehouseManager")
    public ResponseEntity<?> updateWarehouseManager(@RequestBody WarehouseManager warehouseManager) {
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
    @DeleteMapping("/deleteWorker")
    public ResponseEntity<?> deleteWorker(@RequestBody Worker worker) {
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
    @DeleteMapping("/deleteWarehouseManager")
    public ResponseEntity<?> deleteWarehouseManager(@RequestBody WarehouseManager warehouseManager) {
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
    @PatchMapping("/changeWorkerMonthlyPay")
    public ResponseEntity<?> changeWorkerMonthlyPay(@RequestBody PayRiseForm payRiseForm) {
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
    @PatchMapping("/changeWarehouseManagerMonthlyPay")
    public ResponseEntity<?> changeWarehouseManagerMonthlyPay(@RequestBody PayRiseForm payRiseForm) {
        ServiceResponse<WarehouseManager> response = employeeManagerService.changeWarehouseManagerMonthlyPay(payRiseForm);
        if (response.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(response.getMessage());
        }
        return ResponseUtil.okResponse(response.getMessage(), "WarehouseManager", response.getData().get());
    }
}
