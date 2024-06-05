package com.mikolaj.e_library.service;

import com.mikolaj.e_library.DTO.PayRiseForm;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.model.WarehouseManager;
import com.mikolaj.e_library.model.Worker;
import com.mikolaj.e_library.repo.WarehouseManagerRepository;
import com.mikolaj.e_library.repo.WorkerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeManagerServiceTest {
    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WarehouseManagerRepository warehouseManagerRepository;

    @InjectMocks
    private EmployeeManagerService employeeManagerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllWorkers() {
        Worker worker1 = new Worker();
        Worker worker2 = new Worker();
        List<Worker> workers = Arrays.asList(worker1, worker2);

        when(workerRepository.findAll()).thenReturn(workers);

        ServiceResponse<List<Worker>> response = employeeManagerService.getAllWorkers();

        assertTrue(response.getData().isPresent());
        assertEquals(2, response.getData().get().size());
        assertEquals("workers found", response.getMessage());
    }

    @Test
    void getAllWarehouseManagers() {
        WarehouseManager manager1 = new WarehouseManager();
        WarehouseManager manager2 = new WarehouseManager();
        List<WarehouseManager> managers = Arrays.asList(manager1, manager2);

        when(warehouseManagerRepository.findAll()).thenReturn(managers);

        ServiceResponse<List<WarehouseManager>> response = employeeManagerService.getAllWarehouseManagers();

        assertTrue(response.getData().isPresent());
        assertEquals(2, response.getData().get().size());
        assertEquals("warehouseManagers found", response.getMessage());
    }

    @Test
    void getAllWorkers_noWorkersFound() {
        when(workerRepository.findAll()).thenReturn(new ArrayList<>());

        ServiceResponse<List<Worker>> response = employeeManagerService.getAllWorkers();

        assertTrue(response.getData().isEmpty());
        assertEquals("no workers found", response.getMessage());
    }

    @Test
    void getAllWarehouseManagers_noWarehouseManagersFound() {
        when(warehouseManagerRepository.findAll()).thenReturn(new ArrayList<>());

        ServiceResponse<List<WarehouseManager>> response = employeeManagerService.getAllWarehouseManagers();

        assertTrue(response.getData().isEmpty());
        assertEquals("no warehouseManagers found", response.getMessage());
    }

    @Test
    void updateWarehouseManager() {
        WarehouseManager warehouseManager = new WarehouseManager();
        warehouseManager.setWareManId(1);;
        WarehouseManager updateWarehouseManager = new WarehouseManager(1245);
        updateWarehouseManager.setWareManId(1);

        when(warehouseManagerRepository.findById(warehouseManager.getWareManId())).thenReturn(Optional.of(warehouseManager));
        when(warehouseManagerRepository.save(any(WarehouseManager.class))).thenReturn(updateWarehouseManager);

        ServiceResponse<WarehouseManager> response = employeeManagerService.updateWarehouseManager(updateWarehouseManager);

        assertTrue(response.getData().isPresent());
        assertEquals("warehouseManager updated", response.getMessage());
        assertEquals(updateWarehouseManager.getMonthlyPay(), response.getData().get().getMonthlyPay());
        verify(warehouseManagerRepository, times(1)).findById(1);
        verify(warehouseManagerRepository, times(1)).save(any());
    }

    @Test
    void updateWorker() {
        Worker worker = new Worker();
        worker.setWorkerId(1);
        Worker updateWorker = new Worker(1245);
        updateWorker.setWorkerId(1);

        when(workerRepository.findById(worker.getWorkerId())).thenReturn(Optional.of(worker));
        when(workerRepository.save(any(Worker.class))).thenReturn(updateWorker);

        ServiceResponse<Worker> response = employeeManagerService.updateWorker(updateWorker);

        assertTrue(response.getData().isPresent());
        assertEquals("worker updated", response.getMessage());
        assertEquals(updateWorker.getMonthlyPay(), response.getData().get().getMonthlyPay());
        verify(workerRepository, times(1)).findById(1);
        verify(workerRepository, times(1)).save(any());
    }

    @Test
    void deleteWorker() {
        Worker worker = new Worker();
        worker.setWorkerId(1);

        when(workerRepository.existsById(worker.getWorkerId())).thenReturn(true);

        ServiceResponse<Worker> response = employeeManagerService.deleteWorker(worker);

        assertTrue(response.getData().isPresent());
        assertEquals("worker deleted", response.getMessage());
        verify(workerRepository, times(1)).deleteById(worker.getWorkerId());
    }

    @Test
    void deleteWarehouseManager() {
        WarehouseManager manager = new WarehouseManager();
        manager.setWareManId(1);

        when(warehouseManagerRepository.existsById(manager.getWareManId())).thenReturn(true);

        ServiceResponse<WarehouseManager> response = employeeManagerService.deleteWarehouseManager(manager);

        assertTrue(response.getData().isPresent());
        assertEquals("warehouse manager deleted", response.getMessage());
        verify(warehouseManagerRepository, times(1)).deleteById(manager.getWareManId());
    }

    @Test
    void changeWorkerMonthlyPay() {
        PayRiseForm payRiseForm = new PayRiseForm();
        payRiseForm.setWorkerId(1);
        payRiseForm.setMonthlyPay(5000);

        Worker worker = new Worker();
        worker.setWorkerId(1);

        when(workerRepository.existsById(payRiseForm.getWorkerId())).thenReturn(true);
        when(workerRepository.findById(payRiseForm.getWorkerId())).thenReturn(Optional.of(worker));

        ServiceResponse<Worker> response = employeeManagerService.changeWorkerMonthlyPay(payRiseForm);

        assertTrue(response.getData().isPresent());
        assertEquals("worker pay changed", response.getMessage());
        assertEquals(5000, response.getData().get().getMonthlyPay());
    }

    @Test
    void changeWarehouseManagerMonthlyPay() {
        PayRiseForm payRiseForm = new PayRiseForm();
        payRiseForm.setWorkerId(1);
        payRiseForm.setMonthlyPay(7000);

        WarehouseManager manager = new WarehouseManager();
        manager.setWareManId(1);

        when(warehouseManagerRepository.existsById(payRiseForm.getWorkerId())).thenReturn(true);
        when(warehouseManagerRepository.findById(payRiseForm.getWorkerId())).thenReturn(Optional.of(manager));

        ServiceResponse<WarehouseManager> response = employeeManagerService.changeWarehouseManagerMonthlyPay(payRiseForm);

        assertTrue(response.getData().isPresent());
        assertEquals("warehouse manager pay changed", response.getMessage());
        assertEquals(7000, response.getData().get().getMonthlyPay());
    }

    @Test
    void updateWorker_workerNotFound() {
        Worker worker = new Worker();
        worker.setWorkerId(1);

        when(workerRepository.existsById(worker.getWorkerId())).thenReturn(false);

        ServiceResponse<Worker> response = employeeManagerService.updateWorker(worker);

        assertTrue(response.getData().isEmpty());
        assertEquals("worker not found", response.getMessage());
        verify(workerRepository, never()).save(any(Worker.class));
    }

    @Test
    void updateWarehouseManager_warehouseManagerNotFound() {
        WarehouseManager manager = new WarehouseManager();
        manager.setWareManId(1);

        when(warehouseManagerRepository.existsById(manager.getWareManId())).thenReturn(false);

        ServiceResponse<WarehouseManager> response = employeeManagerService.updateWarehouseManager(manager);

        assertTrue(response.getData().isEmpty());
        assertEquals("warehouse manager not found", response.getMessage());
        verify(warehouseManagerRepository, never()).save(any(WarehouseManager.class));
    }

    @Test
    void deleteWorker_workerNotFound() {
        Worker worker = new Worker();
        worker.setWorkerId(1);

        when(workerRepository.existsById(worker.getWorkerId())).thenReturn(false);

        ServiceResponse<Worker> response = employeeManagerService.deleteWorker(worker);

        assertTrue(response.getData().isEmpty());
        assertEquals("worker not found", response.getMessage());
        verify(workerRepository, never()).deleteById(worker.getWorkerId());
    }

    @Test
    void deleteWarehouseManager_warehouseManagerNotFound() {
        WarehouseManager manager = new WarehouseManager();
        manager.setWareManId(1);

        when(warehouseManagerRepository.existsById(manager.getWareManId())).thenReturn(false);

        ServiceResponse<WarehouseManager> response = employeeManagerService.deleteWarehouseManager(manager);

        assertTrue(response.getData().isEmpty());
        assertEquals("warehouse manager not found", response.getMessage());
        verify(warehouseManagerRepository, never()).deleteById(manager.getWareManId());
    }

    @Test
    void changeWorkerMonthlyPay_workerNotFound() {
        PayRiseForm payRiseForm = new PayRiseForm();
        payRiseForm.setWorkerId(1);
        payRiseForm.setMonthlyPay(5000);

        when(workerRepository.existsById(payRiseForm.getWorkerId())).thenReturn(false);

        ServiceResponse<Worker> response = employeeManagerService.changeWorkerMonthlyPay(payRiseForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("worker not found", response.getMessage());
        verify(workerRepository, never()).findById(payRiseForm.getWorkerId());
    }

    @Test
    void changeWarehouseManagerMonthlyPay_warehouseManagerNotFound() {
        PayRiseForm payRiseForm = new PayRiseForm();
        payRiseForm.setWorkerId(1);
        payRiseForm.setMonthlyPay(7000);

        when(warehouseManagerRepository.existsById(payRiseForm.getWorkerId())).thenReturn(false);

        ServiceResponse<WarehouseManager> response = employeeManagerService.changeWarehouseManagerMonthlyPay(payRiseForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("warehouse Manager not found", response.getMessage());
        verify(warehouseManagerRepository, never()).findById(payRiseForm.getWorkerId());
    }
}