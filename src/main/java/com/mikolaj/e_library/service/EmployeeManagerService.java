package com.mikolaj.e_library.service;

import com.mikolaj.e_library.DTO.PayRiseForm;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.model.WarehouseManager;
import com.mikolaj.e_library.model.Worker;
import com.mikolaj.e_library.repo.UserRepository;
import com.mikolaj.e_library.repo.WarehouseManagerRepository;
import com.mikolaj.e_library.repo.WorkerRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeManagerService {
    private WorkerRepository workerRepository;
    private WarehouseManagerRepository warehouseManagerRepository;

    public EmployeeManagerService(WorkerRepository workerRepository, WarehouseManagerRepository warehouseManagerRepository) {
        this.workerRepository = workerRepository;
        this.warehouseManagerRepository = warehouseManagerRepository;
    }

    public ServiceResponse<List<Worker>> getAllWorkers(){
        List<Worker> workers = workerRepository.findAll();
        if(workers.isEmpty()) {
            return new ServiceResponse<>(Optional.empty(), "no workers found");
        }
        return new ServiceResponse<>(Optional.of(workers), "workers found");
    }

    public ServiceResponse<List<WarehouseManager>> getAllWarehouseManagers(){
        List<WarehouseManager> warehouseManagers = warehouseManagerRepository.findAll();
        if(warehouseManagers.isEmpty()) {
            return new ServiceResponse<>(Optional.empty(), "no warehouseManagers found");
        }
        return new ServiceResponse<>(Optional.of(warehouseManagers), "warehouseManagers found");
    }

    public ServiceResponse<Worker> updateWorker(Worker worker) {
        if(!workerRepository.existsById(worker.getWorkerId()))
            return new ServiceResponse<>(Optional.empty(), "worker not found");
        Worker updatedWorker = workerRepository.findById(worker.getWorkerId()).get();
        if(worker.getPesel()!=null) updatedWorker.setPesel(worker.getPesel());
        if(worker.getPayAccountNumber()!=null) updatedWorker.setPayAccountNumber(worker.getPayAccountNumber());
        if(worker.getAddress()!=null) updatedWorker.setAddress(worker.getAddress());
        workerRepository.save(updatedWorker);
        return new ServiceResponse<>(Optional.of(updatedWorker), "worker updated");
    }
    public ServiceResponse<WarehouseManager> updateWarehouseManager(WarehouseManager warehouseManager) {
        if(!warehouseManagerRepository.existsById(warehouseManager.getWareManId()))
            return new ServiceResponse<>(Optional.empty(), "warehouseManager not found");
        WarehouseManager updatedWarehouseManager = warehouseManagerRepository.findById(warehouseManager.getWareManId()).get();
        if(warehouseManager.getPesel()!=null) updatedWarehouseManager.setPesel(warehouseManager.getPesel());
        if(warehouseManager.getPayAccountNumber()!=null) updatedWarehouseManager.setPayAccountNumber(warehouseManager.getPayAccountNumber());
        if(warehouseManager.getAddress()!=null) updatedWarehouseManager.setAddress(warehouseManager.getAddress());
        warehouseManagerRepository.save(updatedWarehouseManager);
        return new ServiceResponse<>(Optional.of(updatedWarehouseManager), "warehouseManager updated");
    }

    public ServiceResponse<Worker> deleteWorker(Worker worker) {
        if(!workerRepository.existsById(worker.getWorkerId()))
            return new ServiceResponse<>(Optional.empty(), "worker not found");
        workerRepository.deleteById(worker.getWorkerId());
        return new ServiceResponse<>(Optional.of(worker), "worker deleted");
    }

    public ServiceResponse<WarehouseManager> deleteWarehouseManager(WarehouseManager warehouseManager) {
        if(!warehouseManagerRepository.existsById(warehouseManager.getWareManId()))
            return new ServiceResponse<>(Optional.empty(), "warehouse manager not found");
        warehouseManagerRepository.deleteById(warehouseManager.getWareManId());
        return new ServiceResponse<>(Optional.of(warehouseManager), "warehouse manager deleted");
    }

    public ServiceResponse<Worker> changeWorkerMonthlyPay(PayRiseForm payRiseForm) {
        if(!workerRepository.existsById(payRiseForm.getWorkerId()))
            return new ServiceResponse<>(Optional.empty(), "worker not found");
        Worker worker = workerRepository.findById(payRiseForm.getWorkerId()).get();
        worker.setMonthlyPay(payRiseForm.getMonthlyPay());
        return new ServiceResponse<>(Optional.of(worker), "worker pay changed");
    }

    public ServiceResponse<WarehouseManager> changeWarehouseManagerMonthlyPay(PayRiseForm payRiseForm) {
        if(!warehouseManagerRepository.existsById(payRiseForm.getWorkerId()))
            return new ServiceResponse<>(Optional.empty(), "warehouse Manager not found");
        WarehouseManager warehouseManager = warehouseManagerRepository.findById(payRiseForm.getWorkerId()).get();
        warehouseManager.setMonthlyPay(payRiseForm.getMonthlyPay());
        warehouseManagerRepository.save(warehouseManager);
        return new ServiceResponse<>(Optional.of(warehouseManager), "warehouse manager pay changed");
    }
}
