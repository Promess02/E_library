package com.mikolaj.e_library.controller;

import com.mikolaj.e_library.DTO.*;
import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.BookRating;
import com.mikolaj.e_library.model.Reader;
import com.mikolaj.e_library.model.Rental;
import com.mikolaj.e_library.repo.BookCopyRepository;
import com.mikolaj.e_library.repo.BookRatingRepository;
import com.mikolaj.e_library.repo.BookRepository;

import com.mikolaj.e_library.repo.ReaderRepository;
import com.mikolaj.e_library.service.ReaderService;
import com.mikolaj.e_library.service.WorkerService;
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
    private final BookRatingRepository bookRatingRepository;
    private final ReaderService readerService;
    private final ReaderRepository readerRepository;
    private final WorkerService workerService;

    public BookController(BookRepository bookRepository, ReaderService readerService, BookRatingRepository bookRatingRepository,
                          ReaderRepository readerRepository, WorkerService workerService) {
        this.bookRepository = bookRepository;
        this.readerService = readerService;
        this.bookRatingRepository = bookRatingRepository;
        this.readerRepository = readerRepository;
        this.workerService = workerService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getBooks(){
        ServiceResponse<?> response = new ServiceResponse<Object>(Optional.of(bookRepository.findAll()), "books found");
        if(response.getData().isEmpty()) return ResponseUtil.okResponse("no books found", "Book", Optional.empty());
        return ResponseUtil.okResponse(response.getMessage(), "Books", response.getData().get());
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
        ServiceResponse<Book> response = new ServiceResponse<>(Optional.of(bookRepository.save(book)),"book saved");
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse("couldn't save book");
        return ResponseUtil.okResponse(response.getMessage(), "Book", response.getData().get());
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

    /*
    Wypożycza książkę(Book.class) o danym id dla czytelnika z danym id na podaną liczbę tygodni
        {
            "readerId": 2,
            "bookId": 4,
            "rentalInWeeks": 5
        }
     */
    @PostMapping("/rent")
    public ResponseEntity<?> rentBook(@RequestBody RentalForm rentalForm){
        ServiceResponse<?> response = readerService.bookRental(rentalForm);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Rental", response.getData().get());
    }


    /*
        Zwraca wszystkie wypożyczenia dla czytelnika. wystarczy id czytelnika
        {
            "readerId": 4
        }
     */
    @GetMapping("/getRentals")
    public ResponseEntity<?> getAllRentalsForReader(@RequestBody Reader reader){
        ServiceResponse<?> response = readerService.getAllRentalsForReader(reader);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Rental", response.getData().get());
    }

    /*
    DZIAŁA
        Zwraca aktywne wypożyczenia dla czytelnika

        {
            "readerId": 6
        }
     */
    @GetMapping("/getActiveRentals")
    public ResponseEntity<?> getActiveRentalsForReader(@RequestBody Reader reader){
        ServiceResponse<?> response = readerService.getAllActiveRentalsForReader(reader);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Rental", response.getData().get());
    }

    /*
    DZIAŁA
        Przedłuża wypożyczenie o podaną liczbę tygodni

        {
            "rentalId": 5,
            "prolongationInWeeks": 6
        }

     */
    @PatchMapping("/prolongateRental")
    public ResponseEntity<?> prolongateRental(@RequestBody ProlongateForm prolongateForm){
        ServiceResponse<?> response = readerService.prolongateRental(prolongateForm);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Rental", response.getData().get());
    }

    /*
        Zwraca stronę przefiltrowanych książek. W body żądania:
        {
            "size": 10,
            "page": 5,
            "filter": "Way", (opcjonalne)
            "author": "Sanderson", (opcjonalne)
            "bookCategory": "Fantasy", (opcjonalne)
            "bookType": "paperback",
            "minBookRating": 2.0
        }

        są dwa sposoby filtrowania:
            - przez filtrowanie nazwy książki, gdzie filter w polu: "filter"
            - przez pozostałe pola
        W przypadku, gdy w polu "filter" jest wartość to pozostałe pola z JSONA są ignorowane,
        nie wszystkie filtry muszą być obecne.
     */
    @GetMapping("/getFilteredBooks")
    public ResponseEntity<?> getFilteredBooks(@RequestBody BookFilter bookFilter){
        ServiceResponse<Page<Book>> response = readerService.getFilteredBooks(bookFilter);
        Optional<Page<Book>> bookPage = response.getData();
        if(bookPage.isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Books", bookPage.get());
    }

    /* dodaje rating użytkownika do książki, jeśli już był wystawiony rating to zmienia go.
           {
            "rating": 4,
            "bookId": 3,
            "readerId": 4
           }
     */
    @PostMapping("/addOrUpdateBookRating")
    public ResponseEntity<?> addOrUpdateBookRating(@RequestBody BookRatingForm bookRatingForm){
        ServiceResponse<?> response = readerService.addOrUpdateBookRating(bookRatingForm);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Rating", response.getData().get());
    }
    /* zwraca wszystkie wiadomości dla czytelnika z danego zakresu dat. Ciało może być puste i wtedy
        zwróci wszystkie wiadomości.
        {
            "startDate": "yyyy-mm-dd",
            "endDate": "yyyy-mm-dd"
        }

        UWAGA: nie testowałem czy rzeczywiście dobrze konwertuje tak podaną datę w ciele żądania,
        ale prawdopodobnie jeszcze nie :(
     */
    @GetMapping("/getNewsPosts")
    public ResponseEntity<?> getAllNewsPosts(@RequestBody DateRange dateRange){
        ServiceResponse<?> response = readerService.getNewsPostsBetweenDates(dateRange);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Posts", response.getData().get());
    }

    /*
        zwraca rating dla danej książki i czytelnika
        {
            "bookId": 4,
            "readerId": 5
        }
     */
    @GetMapping("/getRatingForBook")
    public ResponseEntity<?> getRatingForBook(@RequestBody BookRatingForm bookRatingForm){
        Book book;
        Reader reader;
        if(bookRepository.existsById(bookRatingForm.getBookId())) book =
                bookRepository.findById(bookRatingForm.getBookId()).get();
        else return ResponseUtil.badRequestResponse("no book found");
        if(readerRepository.existsById(bookRatingForm.getReaderId())) reader =
                readerRepository.findById(bookRatingForm.getReaderId()).get();
        else return ResponseUtil.badRequestResponse("no reader found");
        Optional<BookRating> bookRatingDb = bookRatingRepository.findByBookAndReader(book,reader);
        return bookRatingDb.map(rating -> ResponseUtil.okResponse("book rating found", "Book rating", rating))
                .orElseGet(() -> ResponseUtil.badRequestResponse("No rating present"));
    }
}