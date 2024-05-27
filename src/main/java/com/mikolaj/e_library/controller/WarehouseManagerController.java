package com.mikolaj.e_library.controller;

import com.mikolaj.e_library.DTO.ResponseUtil;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.BookCopy;
import com.mikolaj.e_library.service.WarehouseManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warehouseManager")
@CrossOrigin(origins = "http://localhost:3000") // Add @CrossOrigin annotation to enable CORS for this method
public class WarehouseManagerController {
    private WarehouseManagerService warehouseManagerService;

    public WarehouseManagerController(WarehouseManagerService warehouseManagerService) {
        this.warehouseManagerService = warehouseManagerService;
    }

    @PostMapping("/addBook")
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        ServiceResponse<Book> serviceResponse = warehouseManagerService.addBook(book);
        if(serviceResponse.getData().isEmpty()) return ResponseUtil.badRequestResponse(serviceResponse.getMessage());
        return ResponseUtil.okResponse(serviceResponse.getMessage(),"Book", serviceResponse.getData().get());

    }

    @PutMapping("/updateBook")
    public ResponseEntity<?> updateBook(@RequestBody Book book) {
        ServiceResponse<Book> serviceResponse = warehouseManagerService.updateBook(book);
        if (serviceResponse.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(serviceResponse.getMessage());
        }
        return ResponseUtil.okResponse(serviceResponse.getMessage(), "Book", serviceResponse.getData().get());
    }

    @DeleteMapping("/deleteBook")
    public ResponseEntity<?> deleteBook(@RequestBody Book book) {
        ServiceResponse<Book> serviceResponse = warehouseManagerService.deleteBook(book);
        if (serviceResponse.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(serviceResponse.getMessage());
        }
        return ResponseUtil.okResponse(serviceResponse.getMessage(), "Book", serviceResponse.getData().get());
    }

    @PostMapping("/addBookCopy")
    public ResponseEntity<?> addBookCopy(@RequestBody BookCopy bookCopy) {
        ServiceResponse<BookCopy> serviceResponse = warehouseManagerService.addBookCopy(bookCopy);
        if (serviceResponse.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(serviceResponse.getMessage());
        }
        return ResponseUtil.okResponse(serviceResponse.getMessage(), "BookCopy", serviceResponse.getData().get());
    }

    @PutMapping("/updateBookCopy")
    public ResponseEntity<?> updateBookCopy(@RequestBody BookCopy bookCopy) {
        ServiceResponse<BookCopy> serviceResponse = warehouseManagerService.updateBookCopy(bookCopy);
        if (serviceResponse.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(serviceResponse.getMessage());
        }
        return ResponseUtil.okResponse(serviceResponse.getMessage(), "BookCopy", serviceResponse.getData().get());
    }

    @DeleteMapping("/deleteBookCopy")
    public ResponseEntity<?> deleteBookCopy(@RequestBody BookCopy bookCopy) {
        ServiceResponse<BookCopy> serviceResponse = warehouseManagerService.deleteBookCopy(bookCopy);
        if (serviceResponse.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(serviceResponse.getMessage());
        }
        return ResponseUtil.okResponse(serviceResponse.getMessage(), "BookCopy", serviceResponse.getData().get());
    }
}
