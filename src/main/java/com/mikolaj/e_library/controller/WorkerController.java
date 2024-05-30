package com.mikolaj.e_library.controller;

import com.mikolaj.e_library.DTO.*;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.repo.*;
import com.mikolaj.e_library.service.ReaderService;
import com.mikolaj.e_library.service.WorkerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/worker")
@CrossOrigin(origins = "http://localhost:3000") // Add @CrossOrigin annotation to enable CORS for this method
public class WorkerController {
    private final BookRepository bookRepository;
    private final BookRatingRepository bookRatingRepository;
    private final BookCopyRepository bookCopyRepository;
    private final ReaderService readerService;
    private final ReaderRepository readerRepository;
    private final WorkerService workerService;
    private final NewsPostRepository newsPostRepository;
    private final WorkerRepository workerRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public WorkerController(BookRepository bookRepository, BookRatingRepository bookRatingRepository, BookCopyRepository bookCopyRepository, ReaderService readerService, ReaderRepository readerRepository, WorkerService workerService, NewsPostRepository newsPostRepository, WorkerRepository workerRepository, RentalRepository rentalRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.readerService = readerService;
        this.readerRepository = readerRepository;
        this.workerService = workerService;
        this.newsPostRepository = newsPostRepository;
        this.workerRepository = workerRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    /*
Zwraca książkę dla danego wypożyczenia. Wystarczy podać id w żądaniu
    {
        "rentalId": 5
    }
 */
    @PatchMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody Rental rental){
        ServiceResponse<?> response = workerService.returnBook(rental);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Rental", response.getData().get());
    }

    /*
Wypożycza książkę(Book.class) o danym id dla czytelnika z danym id na podaną liczbę tygodni
    {
        "workerId": 4
        "readerEmail": "userEmail@wp.pl",
        "bookId": 4,
        "rentalInWeeks": 5
    }
 */
    @PostMapping("/rent")
    public ResponseEntity<?> rentBook(@RequestBody WorkerRentalForm rentalForm){
        ServiceResponse<?> response = workerService.rentBookForReader(rentalForm);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Rental", response.getData().get());
    }

    /*
    {
    "workerId": 1,
    "name": "nowy post",
    "contents": "zawartosc",
    "imageUrl": "/newimageurl"
    }
 */
    @PostMapping("/addNewsPost")
    public ResponseEntity<?> addNewsPost(@RequestBody AddNewsPostForm addNewsPostForm){
        ServiceResponse<?> response = workerService.addNewsPost(addNewsPostForm);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "News post", response.getData().get());
    }


    /*
    {
    "workerId": 1,
    "name": "nowy post",
    "newsPostId": 1,
    "contents": "zmieniona zawartość",
    "imageUrl": "/newimageurl"
    }
 */
    @PutMapping("/updateNewsPost")
    public ResponseEntity<?> updateNewsPost(@RequestBody AddNewsPostForm addNewsPostForm){
        ServiceResponse<?> response = workerService.updateNewsPost(addNewsPostForm);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "News post", response.getData().get());
    }

    /*
    W url żądania podajemy id posta do usunięcia. Przykład url:
    http://localhost:8080/worker/deleteNewsPost/post=1
 */
    @DeleteMapping("/deleteNewsPost/postId={postId}")
    public ResponseEntity<?> deleteNewsPost(@PathVariable int postId){
        ServiceResponse<?> response = workerService.deleteNewsPost(postId);
        if(response.getData().isEmpty())  return ResponseUtil.badRequestResponse("post not found");
        else return ResponseUtil.okResponse(response.getMessage(), "News Post: ", response.getData().get());
    }

    @GetMapping("/getReaders")
    public ResponseEntity<?> getAllReaders(){
        List<Reader> readers = readerRepository.findAll();
        if(readers.isEmpty()) return ResponseUtil.badRequestResponse("no readers found");
        return ResponseUtil.okResponse("found readers", "Readers: ", readers);
    }

    @GetMapping("/getNewsPosts")
    public ResponseEntity<?> getAllNewsPosts(){
        List<NewsPost> readers = newsPostRepository.findAll();
        if(readers.isEmpty()) return ResponseUtil.badRequestResponse("no news posts found");
        return ResponseUtil.okResponse("found news posts", "News posts: ", readers);
    }

    @GetMapping("/getRentalsForUser/email={userEmail}")
    public ResponseEntity<?> getActiveRentalsForUser(@PathVariable String userEmail){
        if(!userRepository.existsByEmail(userEmail)) return ResponseUtil.badRequestResponse("No user with given email found");
        List<Rental> rentals = rentalRepository.findAllByReaderUserEmailAndStatus(userEmail, RentalStatus.ACTIVE);
        return  ResponseUtil.okResponse("found rentals", "Rentals: ", rentals);
    }

    @GetMapping("/getAllRentalsForUser/email={userEmail}")
    public ResponseEntity<?> getAllRentalsForUser(@PathVariable String userEmail){
        if(!userRepository.existsByEmail(userEmail)) return ResponseUtil.badRequestResponse("No user with given email found");
        List<Rental> rentals = rentalRepository.findAllByReaderUserEmail(userEmail);
        return  ResponseUtil.okResponse("found rentals", "Rentals: ", rentals);
    }

    /*
        {
        "filter": "ada",
        "filterBy": "title",
        "page": 0,
        "size": 3
        }
    */
//    @PostMapping("/getAllRentalsForUser/email={userEmail}/paginated")
//    public ResponseEntity<?> getAllRentalsForUserPaginated(@PathVariable String userEmail, @RequestBody Pagination pagination) {
//        Page<Rental> rentalsPage = rentalRepository.findByReaderUserEmail(userEmail, PageRequest.of(pagination.getPage(), pagination.getSize()));
//
//        if (rentalsPage.isEmpty()) {
//            return ResponseUtil.okResponse("no rentals found", "Rentals", Optional.empty());
//        }
//        return ResponseUtil.okResponse("rentals found: ", "Rentals", rentalsPage);
//    }

    /*
        {
        "filter": "ada",
        "filterBy": "title",
        "page": 0,
        "size": 3
        }
    */
    @PostMapping("/getAllRentalsForUser/email={userEmail}/paginated")
    public ResponseEntity<?> getAllRentalsForUserPaginated(@PathVariable String userEmail, @RequestBody Pagination pagination) {
        Page<Rental> rentalsPage;
        if (pagination.getFilterBy().isEmpty()) {
            rentalsPage = rentalRepository.findByReaderUserEmail(userEmail, PageRequest.of(pagination.getPage(), pagination.getSize()));
        }
        else if (pagination.getFilterBy().equals("active")){
            rentalsPage = rentalRepository.findByReaderUserEmailAndStatus(userEmail, RentalStatus.ACTIVE ,PageRequest.of(pagination.getPage(), pagination.getSize()));
        }
        else if (pagination.getFilterBy().equals("inactive")){
            rentalsPage = rentalRepository.findByReaderUserEmailAndStatus(userEmail, RentalStatus.INACTIVE ,PageRequest.of(pagination.getPage(), pagination.getSize()));
        }
        else {
            rentalsPage = rentalRepository.findByReaderUserEmail(userEmail, PageRequest.of(pagination.getPage(), pagination.getSize()));
        }
        if (rentalsPage.isEmpty()) {
            return ResponseUtil.okResponse("no rentals found", "Rentals", Optional.empty());
        }
        return ResponseUtil.okResponse("rentals found: ", "Rentals", rentalsPage);
    }



}
