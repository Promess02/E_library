package com.mikolaj.e_library.controller;

import com.mikolaj.e_library.DTO.ResponseUtil;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.repo.BookCopyRepository;
import com.mikolaj.e_library.repo.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/book")
@CrossOrigin(origins = "http://localhost:3000") // Add @CrossOrigin annotation to enable CORS for this method
public class BookController {
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    public BookController(BookRepository bookRepository, BookCopyRepository bookCopyRepository) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getBooks(){
        ServiceResponse<?> response = new ServiceResponse<Object>(Optional.of(bookRepository.findAll()), "books found");
        if(response.getData().isEmpty()) return ResponseUtil.okResponse("no activities found", "Activity", Optional.empty());
        return ResponseUtil.okResponse(response.getMessage(), "Books", response.getData());
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveBook(@RequestBody Book book){
        ServiceResponse<?> response = new ServiceResponse<Object>(Optional.of(bookRepository.save(book)),"book saved");
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse("couldn't save book");
        return ResponseUtil.okResponse(response.getMessage(), "Book", response.getData());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBook(@RequestBody Book book){
        bookRepository.delete(book);
        return ResponseUtil.okResponse("book saved", "Book", book);
    }


}
