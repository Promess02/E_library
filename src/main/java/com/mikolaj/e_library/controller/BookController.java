package com.mikolaj.e_library.controller;

import com.mikolaj.e_library.DTO.*;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.repo.BookCopyRepository;
import com.mikolaj.e_library.repo.BookRatingRepository;
import com.mikolaj.e_library.repo.BookRepository;

import com.mikolaj.e_library.repo.ReaderRepository;
import com.mikolaj.e_library.service.ReaderService;
import com.mikolaj.e_library.service.RegistrationService;
import com.mikolaj.e_library.service.WorkerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final RegistrationService registrationService;

    public BookController(BookRepository bookRepository, ReaderService readerService, BookRatingRepository bookRatingRepository,
                          ReaderRepository readerRepository, WorkerService workerService, RegistrationService registrationService) {
        this.bookRepository = bookRepository;
        this.readerService = readerService;
        this.bookRatingRepository = bookRatingRepository;
        this.readerRepository = readerRepository;
        this.workerService = workerService;
        this.registrationService = registrationService;
    }

    @GetMapping("/getAll/apiKey={apiKey}")
    public ResponseEntity<?> getBooks(@PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader", "worker", "warehouse manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
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
    @PostMapping("/getAllPaginated/apiKey={apiKey}")
    public ResponseEntity<?> getBooks(@RequestBody Pagination pagination, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader", "worker", "employee manager", "warehouse manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
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
        "bookId": 2,
        }
     */
    @PatchMapping("/reserveBook/apiKey={apiKey}")
    public ResponseEntity<?> reserveBook(@RequestBody BookReservation reservation, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader", "worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        reservation.setReaderId(registrationService.getWorkerTypeIdForApiKey(apiKey));
        ServiceResponse<BookCopy> response = readerService.reserveBook(reservation.getBookId(), reservation.getReaderId());
        if(response.getData().isEmpty()) return  ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "BookCopy", response.getData());
    }

/*
Email może być pusty i wtedy wiemy, że użytkownik wysyła, jeżwki coś jest to pracownik
 {
    "readerEmail": ""
 }
 */
    @GetMapping("/getAllReserved/apiKey={apiKey}")
    public ResponseEntity<?> getAllReservedForReader(@RequestBody BookReservation reservation, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader", "worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        int readerId = registrationService.getWorkerTypeIdForApiKey(apiKey);
        if(reservation.getReaderEmail()!=null && !reservation.getReaderEmail().isEmpty() &&
                registrationService.getWorkerTypeForApiKey(apiKey).equals("worker")){
            Optional<Reader> reader = readerRepository.findByUserEmail(reservation.getReaderEmail());
            if (reader.isEmpty()) return ResponseUtil.badRequestResponse("No reader found with given email");
            readerId = reader.get().getReaderId();
        }
        ServiceResponse<List<BookCopy>> response = readerService.getReservedCopiesForReader(readerId);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Copy list", response.getData());
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
    @PostMapping("/save/apiKey={apiKey}")
    public ResponseEntity<?> saveBook(@RequestBody Book book, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("warehouse manager","worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<Book> response = new ServiceResponse<>(Optional.of(bookRepository.save(book)),"book saved");
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse("couldn't save book");
        return ResponseUtil.okResponse(response.getMessage(), "Book", response.getData().get());
    }

    /*
       {
        "bookId": 9,
       }
    */
    @DeleteMapping("/delete/apiKey={apiKey}")
    public ResponseEntity<?> deleteBook(@RequestBody Book book, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("warehouse manager","worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        if (bookRepository.findById(book.getBookId()).isPresent()) {
            bookRepository.delete(book);
            return ResponseUtil.okResponse("book deleted", "Book", book);
        }
        return ResponseUtil.idNotFoundResponse(Book.class);
    }

    /*
    Wypożycza książkę(Book.class) o danym id dla czytelnika z danym id na podaną liczbę tygodni
        {
            "bookId": 4,
            "rentalInWeeks": 5
            "readerEmail": "miko"
        }
     */
    @PostMapping("/rent/apiKey={apiKey}")
    public ResponseEntity<?> rentBook(@RequestBody RentalForm rentalForm, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader","worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        String email = processEmail(rentalForm.getReaderEmail(),apiKey);
        rentalForm.setReaderEmail(email);
        ServiceResponse<?> response = readerService.bookRental(rentalForm);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Rental", response.getData().get());
    }


    /*
        Zwraca wszystkie wypożyczenia dla czytelnika. wystarczy email czytelnika lub jeśli apiKey należy do czytelnika to nie trzeba nic
        {
            "email": "miko"
        }
     */
    @GetMapping("/getRentals/apiKey={apiKey}")
    public ResponseEntity<?> getAllRentalsForReader(@RequestBody User user, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader","worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        String readerEmail = registrationService.getUserEmailForApiKey(apiKey);
        if(!user.getEmail().isEmpty() || registrationService.getWorkerTypeForApiKey(apiKey).equals("worker"))
            readerEmail = user.getEmail();
        ServiceResponse<?> response = readerService.getAllRentalsForReader(readerEmail);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Rental", response.getData().get());
    }

    /*
    DZIAŁA
        Zwraca aktywne wypożyczenia dla czytelnika

        {
            "email": "miko"
        }
     */
    @GetMapping("/getActiveRentals/apiKey={apiKey}")
    public ResponseEntity<?> getActiveRentalsForReader(@RequestBody User user, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader","worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        String readerEmail = registrationService.getUserEmailForApiKey(apiKey);
        if(!user.getEmail().isEmpty() || registrationService.getWorkerTypeForApiKey(apiKey).equals("worker"))
            readerEmail = user.getEmail();
        ServiceResponse<?> response = readerService.getAllActiveRentalsForReader(readerEmail);
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
    @PatchMapping("/prolongateRental/apiKey={apiKey}")
    public ResponseEntity<?> prolongateRental(@RequestBody ProlongateForm prolongateForm, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader","worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
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
    @GetMapping("/getFilteredBooks/apiKey={apiKey}")
    public ResponseEntity<?> getFilteredBooks(@RequestBody BookFilter bookFilter, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader","worker","warehouse manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<Page<Book>> response = readerService.getFilteredBooks(bookFilter);
        Optional<Page<Book>> bookPage = response.getData();
        if(bookPage.isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Books", bookPage.get());
    }

    /* dodaje rating użytkownika do książki, jeśli już był wystawiony rating to zmienia go.
           {
            "rating": 4,
            "bookId": 3
           }
     */
    @PostMapping("/addOrUpdateBookRating/apiKey={apiKey}")
    public ResponseEntity<?> addOrUpdateBookRating(@RequestBody BookRatingForm bookRatingForm, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader"))) return ResponseUtil.badRequestResponse("Authentication failed");
        bookRatingForm.setReaderId(registrationService.getWorkerTypeIdForApiKey(apiKey));
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

     */
    @GetMapping("/getNewsPosts/apiKey={apiKey}")
    public ResponseEntity<?> getAllNewsPosts(@RequestBody DateRange dateRange, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader","worker","warehouse manager", "employee manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<?> response = readerService.getNewsPostsBetweenDates(dateRange);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Posts", response.getData().get());
    }

    /*
        zwraca rating dla danej książki i czytelnika
        {
            "bookId": 4,
        }
     */
    @GetMapping("/getRatingForBook/apiKey={apiKey}")
    public ResponseEntity<?> getRatingForBook(@RequestBody BookRatingForm bookRatingForm, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader","worker","warehouse manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        bookRatingForm.setReaderId(registrationService.getWorkerTypeIdForApiKey(apiKey));
        Book book;
        Reader reader;
        if(bookRepository.existsById(bookRatingForm.getBookId()))
            book = bookRepository.findById(bookRatingForm.getBookId()).get();
        else return ResponseUtil.badRequestResponse("no book found");
        if(readerRepository.existsById(bookRatingForm.getReaderId()))
            reader = readerRepository.findById(bookRatingForm.getReaderId()).get();
        else return ResponseUtil.badRequestResponse("no reader found");
        Optional<BookRating> bookRatingDb = bookRatingRepository.findByBookAndReader(book,reader);
        return bookRatingDb.map(rating -> ResponseUtil.okResponse("book rating found", "Book rating", rating))
                .orElseGet(() -> ResponseUtil.badRequestResponse("No rating present"));
    }
    // Używamy tego, bo wypożyczenia może pobierać czytelnik i pracownik
    private String processEmail(String userEmail, String apiKey){
        //dla czytelnika
        String email = registrationService.getUserEmailForApiKey(apiKey);
        // dla pracownika
        if(userEmail!=null || registrationService.getWorkerTypeForApiKey(apiKey).equals("worker"))
            email = userEmail;
        return email;
    }

}