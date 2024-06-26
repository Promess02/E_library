package com.mikolaj.e_library.controller;

import com.mikolaj.e_library.DTO.*;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.repo.*;
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
    private final RegistrationService registrationService;

    public WorkerController(BookRepository bookRepository, BookRatingRepository bookRatingRepository,
                            BookCopyRepository bookCopyRepository, ReaderService readerService,
                            ReaderRepository readerRepository, WorkerService workerService,
                            NewsPostRepository newsPostRepository, WorkerRepository workerRepository, RentalRepository rentalRepository,
                            UserRepository userRepository, RegistrationService registrationService) {
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
        this.registrationService = registrationService;
    }

    /*
Zwraca książkę dla danego wypożyczenia. Wystarczy podać id w żądaniu
    {
        "rentalId": 5
    }
 */
    @PatchMapping("/return/apiKey={apiKey}")
    public ResponseEntity<?> returnBook(@RequestBody Rental rental, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader", "worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<?> response = workerService.returnBook(rental);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Rental", response.getData().get());
    }

    /*
Wypożycza książkę(Book.class) o danym id dla czytelnika z danym id na podaną liczbę tygodni
    {
        "readerEmail": "userEmail@wp.pl",
        "bookId": 4,
        "rentalInWeeks": 5
    }
 */
    @PostMapping("/rent/apiKey={apiKey}")
    public ResponseEntity<?> rentBook(@RequestBody WorkerRentalForm rentalForm, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("reader", "worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        rentalForm.setWorkerId(registrationService.getWorkerTypeIdForApiKey(apiKey));
        ServiceResponse<?> response = workerService.rentBookForReader(rentalForm);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "Rental", response.getData().get());
    }

    /*
    {
    "name": "nowy post",
    "contents": "zawartosc",
    "imageUrl": "/newimageurl"
    }
 */
    @PostMapping("/addNewsPost/apiKey={apiKey}")
    public ResponseEntity<?> addNewsPost(@RequestBody AddNewsPostForm addNewsPostForm, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        addNewsPostForm.setWorkerId(registrationService.getWorkerTypeIdForApiKey(apiKey));
        ServiceResponse<?> response = workerService.addNewsPost(addNewsPostForm);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "News post", response.getData().get());
    }


    /*
    {
    "name": "nowy post",
    "newsPostId": 1,
    "contents": "zmieniona zawartość",
    "imageUrl": "/newimageurl"
    }
 */
    @PutMapping("/updateNewsPost/apiKey={apiKey}")
    public ResponseEntity<?> updateNewsPost(@RequestBody AddNewsPostForm addNewsPostForm, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        addNewsPostForm.setWorkerId(registrationService.getWorkerTypeIdForApiKey(apiKey));
        ServiceResponse<?> response = workerService.updateNewsPost(addNewsPostForm);
        if(response.getData().isEmpty()) return ResponseUtil.badRequestResponse(response.getMessage());
        return ResponseUtil.okResponse(response.getMessage(), "News post", response.getData().get());
    }

    /*
    W url żądania podajemy id posta do usunięcia. Przykład url:
    http://localhost:8080/worker/deleteNewsPost/post=1
 */
    @DeleteMapping("/deleteNewsPost/postId={postId}/apiKey={apiKey}")
    public ResponseEntity<?> deleteNewsPost(@PathVariable int postId, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        ServiceResponse<?> response = workerService.deleteNewsPost(postId);
        if(response.getData().isEmpty())  return ResponseUtil.badRequestResponse("post not found");
        else return ResponseUtil.okResponse(response.getMessage(), "News Post: ", response.getData().get());
    }

    @GetMapping("/getReaders/apiKey={apiKey}")
    public ResponseEntity<?> getAllReaders(@PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        List<Reader> readers = readerRepository.findAll();
        if(readers.isEmpty()) return ResponseUtil.badRequestResponse("no readers found");
        return ResponseUtil.okResponse("found readers", "Readers: ", readers);
    }


    /*
        {
        "filter": "ada",
        "filterBy": "title",
        "page": 0,
        "size": 3
        }
    */
    @PostMapping("/getReadersPaginated/apiKey={apiKey}")
    public ResponseEntity<?> getAllReadersPaginated(@RequestBody Pagination pagination, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("worker"))) return ResponseUtil.badRequestResponse("Authentication failed");
        Page<Reader> readersPage;

        if (pagination.getFilterBy().isEmpty()) {
            readersPage = readerRepository.findAll(PageRequest.of(pagination.getPage(), pagination.getSize()));
        }
        else if (pagination.getFilterBy().equals("email")) {
            readersPage = readerRepository.findByUserEmailStartingWithIgnoreCase(pagination.getFilter(), PageRequest.of(pagination.getPage(), pagination.getSize()));
        }
        else if (pagination.getFilterBy().equals("surname")){
            readersPage = readerRepository.findByUserSurnameStartingWithIgnoreCase(pagination.getFilter(), PageRequest.of(pagination.getPage(), pagination.getSize()));
        }
        else{
            readersPage = readerRepository.findAll(PageRequest.of(pagination.getPage(), pagination.getSize()));
        }

        if (readersPage.isEmpty()) {
            return ResponseUtil.okResponse("no readers found", "Reader", Optional.empty());
        }
        return ResponseUtil.okResponse("readers found: ", "Reader", readersPage);
    }

    @GetMapping("/getNewsPosts/apiKey={apiKey}")
    public ResponseEntity<?> getAllNewsPosts(@PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("worker", "reader", "employee manager", "warehouse manager"))) return ResponseUtil.badRequestResponse("Authentication failed");
        List<NewsPost> readers = newsPostRepository.findAll();
        if(readers.isEmpty()) return ResponseUtil.badRequestResponse("no news posts found");
        return ResponseUtil.okResponse("found news posts", "News posts: ", readers);
    }

    // Zwraca wszystkie rentals dla podanego maila w linku
    @GetMapping("/getRentalsForUser/email={userEmail}/apiKey={apiKey}")
    public ResponseEntity<?> getActiveRentalsForUser(@PathVariable String userEmail, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("worker", "reader"))) return ResponseUtil.badRequestResponse("Authentication failed");
        String email = processEmail(userEmail, apiKey);
        if(!userRepository.existsByEmail(email)) return ResponseUtil.badRequestResponse("No user with given email found");
        List<Rental> rentals = rentalRepository.findAllByReaderUserEmailAndStatus(email, RentalStatus.ACTIVE);
        return  ResponseUtil.okResponse("found rentals", "Rentals: ", rentals);
    }

    // Zwraca wszystkie ACTIVE rentals dla podanego maila w linku
    @GetMapping("/getAllRentalsForUser/email={userEmail}/apiKey={apiKey}")
    public ResponseEntity<?> getAllRentalsForUser(@PathVariable String userEmail, @PathVariable String apiKey){
        if(registrationService.handleAuthentication(apiKey, List.of("worker", "reader"))) return ResponseUtil.badRequestResponse("Authentication failed");
        String email =processEmail(userEmail, apiKey);
        if(!userRepository.existsByEmail(email)) return ResponseUtil.badRequestResponse("No user with given email found");
        List<Rental> rentals = rentalRepository.findAllByReaderUserEmail(email);
        return  ResponseUtil.okResponse("found rentals", "Rentals: ", rentals);
    }

    /*
        {
        "filter": "ada",
        "filterBy": "title",
        "page": 0,
        "size": 3
        }
        Działa w ten sposób że podajemy email i dla podanego emaila zwracamy wszystkie rentals, mozemy dodac filterBy w pagination
        W ten sposob mozemy filtrowac status
    */
    // POLECAM NIE RUSZAC WYDAJE SIE DOBRZE DZIALAC
    // SORRY MUSIAŁ API KEY DODAĆ :'(
    @PostMapping("/getAllRentalsForUser/email={email}/paginated/apiKey={apiKey}")
    public ResponseEntity<?> getAllRentalsForUserPaginated(@PathVariable String email, @RequestBody Pagination pagination, @PathVariable String apiKey) {
        if(registrationService.handleAuthentication(apiKey, List.of("worker", "reader"))) return ResponseUtil.badRequestResponse("Authentication failed");
        Page<Rental> rentalsPage;
        String userEmail = processEmail(email, apiKey);
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
