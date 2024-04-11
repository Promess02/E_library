package com.mikolaj.e_library.controller;

import com.mikolaj.e_library.DTO.Pagination;
import com.mikolaj.e_library.DTO.ResponseUtil;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.repo.BookCopyRepository;
import com.mikolaj.e_library.repo.BookRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    
    /*
        {
        "filter": "ada",
        "filterBy": "title",
        "page": 0,
        "size": 3
        }
    */
    @PostMapping("/getAllPaginated")
    public ResponseEntity<?> getBooks(@RequestBody Pagination pagination){
        Page<Book> booksPage;

        if (pagination.getFilterBy().isEmpty()) {
            booksPage = bookRepository.findAll(PageRequest.of(pagination.getPage(), pagination.getSize()));
        } else if (pagination.getFilterBy().equals("title")) {
            booksPage = bookRepository.findByTitleContainingIgnoreCase(pagination.getFilter(), PageRequest.of(pagination.getPage(), pagination.getSize(), Sort.by(pagination.getFilterBy()).ascending()));
        } else {
            booksPage = bookRepository.findByBookAuthorContainingIgnoreCase(pagination.getFilter(), PageRequest.of(pagination.getPage(), pagination.getSize(), Sort.by(pagination.getFilterBy()).ascending()));
        }

        if (booksPage.isEmpty()) {
            return ResponseUtil.okResponse("no books found", "Books", Optional.empty());
        }
        return ResponseUtil.okResponse("books found: ", "Books", booksPage);
    }

    /*
       {
        "bookId": 9,
        "bookType": "kt",
        "title": "siema",
        "releaseDate": "2022-01-01",
        "bookCategory": "bk",
        "averageBookRating": 5.0,
        "imageUrl": "costmta.jpg",
        "bookAuthor": "Rojek",
        "description": "blebleblbel"
       }
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveBook(@RequestBody Book book){
        ServiceResponse<?> response = new ServiceResponse<Object>(Optional.of(bookRepository.save(book)),"book saved");
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse("couldn't save book");
        return ResponseUtil.okResponse(response.getMessage(), "Book", response.getData());
    }

    /*
       {
        "bookId": 9,
       }
    */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBook(@RequestBody Book book){
        if (bookRepository.findById(book.getBookId()).isPresent()) {
            bookRepository.delete(book);
            return ResponseUtil.okResponse("book deleted", "Book", book);
        }
        return ResponseUtil.idNotFoundResponse(Book.class);
    }


}
