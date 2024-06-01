package com.mikolaj.e_library.controller;

import com.mikolaj.e_library.DTO.CopiesForm;
import com.mikolaj.e_library.DTO.ResponseUtil;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.BookCopy;
import com.mikolaj.e_library.service.RegistrationService;
import com.mikolaj.e_library.service.WarehouseManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouseManager")
@CrossOrigin(origins = "http://localhost:3000") // Add @CrossOrigin annotation to enable CORS for this method
public class WarehouseManagerController {
    private WarehouseManagerService warehouseManagerService;
    private RegistrationService registrationService;

    public WarehouseManagerController(WarehouseManagerService warehouseManagerService, RegistrationService registrationService) {
        this.warehouseManagerService = warehouseManagerService;
        this.registrationService = registrationService;
    }
/*
    {
            "bookType": "paperback",
            "title": "siema",
            "releaseDate": "2022-01-01",
            "bookCategory": "fantasy",
            "imageUrl": "costmta.jpg",
            "bookAuthor": "Rojek",
            "description": "blebleblbel"
    }
    */
    @PostMapping("/addBook/apiKey={apiKey}")
    public ResponseEntity<?> addBook(@RequestBody Book book, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("warehouse manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<Book> serviceResponse = warehouseManagerService.addBook(book);
        if(serviceResponse.getData().isEmpty()) return ResponseUtil.badRequestResponse(serviceResponse.getMessage());
        return ResponseUtil.okResponse(serviceResponse.getMessage(),"Book", serviceResponse.getData().get());

    }

   /*
   {
        "bookId": 2,
            "bookType": "paperback",
            "title": "siema",
            "releaseDate": "2022-01-01",
            "bookCategory": "fantasy",
            "imageUrl": "costmta.jpg",
            "bookAuthor": "Rojek",
            "description": "blebleblbel"
    }
    */
    @PutMapping("/updateBook/apiKey={apiKey}")
    public ResponseEntity<?> updateBook(@RequestBody Book book, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("warehouse manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<Book> serviceResponse = warehouseManagerService.updateBook(book);
        if (serviceResponse.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(serviceResponse.getMessage());
        }
        return ResponseUtil.okResponse(serviceResponse.getMessage(), "Book", serviceResponse.getData().get());
    }

    /*
    {
        "bookId": 2
    }
     */
    @DeleteMapping("/deleteBook/apiKey={apiKey}")
    public ResponseEntity<?> deleteBook(@RequestBody Book book, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("warehouse manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<Book> serviceResponse = warehouseManagerService.deleteBook(book);
        if (serviceResponse.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(serviceResponse.getMessage());
        }
        return ResponseUtil.okResponse(serviceResponse.getMessage(), "Book", serviceResponse.getData().get());
    }
    /*
        {
            "bookId": 3,
            "shelfPlace": "4g",
            "workerId": 1
        }
     */
    @PostMapping("/addBookCopy/copies={numberOfCopies}/apiKey={apiKey}")
    public ResponseEntity<?> addBookCopy(@RequestBody CopiesForm copiesForm, @PathVariable Integer numberOfCopies, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("warehouse manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<BookCopy> serviceResponse = warehouseManagerService.addBookCopy(copiesForm, numberOfCopies);
        if (serviceResponse.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(serviceResponse.getMessage());
        }
        return ResponseUtil.okResponse(serviceResponse.getMessage(), "BookCopy", serviceResponse.getData().get());
    }
    /*
    {
        "copyId": 4,
        "shelfPlace": "5f",
        "bookId": 2,
        // UWAGA RENTALSTATUS TO ENUM I PRZYJMUJE TYLKO JEDNA Z WARTOŚCI ENUMA RENTALSTATUS
        "rentalStatus": "active",
        "qualityStatus": "pretty bad",
        "workerId": 2
    }
    */
    @PutMapping("/updateBookCopy/apiKey={apiKey}")
    public ResponseEntity<?> updateBookCopy(@RequestBody CopiesForm copiesForm, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("warehouse manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<BookCopy> serviceResponse = warehouseManagerService.updateBookCopy(copiesForm);
        if (serviceResponse.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(serviceResponse.getMessage());
        }
        return ResponseUtil.okResponse(serviceResponse.getMessage(), "BookCopy", serviceResponse.getData().get());
    }

    /*
    {
        "copyId": 3
    }
     */
    @DeleteMapping("/deleteBookCopy/apiKey={apiKey}")
    public ResponseEntity<?> deleteBookCopy(@RequestBody BookCopy bookCopy, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("warehouse manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<BookCopy> serviceResponse = warehouseManagerService.deleteBookCopy(bookCopy);
        if (serviceResponse.getData().isEmpty()) {
            return ResponseUtil.badRequestResponse(serviceResponse.getMessage());
        }
        return ResponseUtil.okResponse(serviceResponse.getMessage(), "BookCopy", serviceResponse.getData().get());
    }
}
