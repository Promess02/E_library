package com.mikolaj.e_library.service;

import com.mikolaj.e_library.Consts;
import com.mikolaj.e_library.DTO.*;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorkerServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private ReaderRepository readerRepository;
    @Mock
    private BookCopyRepository bookCopyRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private BookRatingRepository bookRatingRepository;
    @Mock
    private NewsPostRepository newsPostRepository;

    @Mock
    private WorkerRepository workerRepository;

    @InjectMocks
    private WorkerService workerService;

    private WorkerRentalForm workerRentalForm;
    private User user;
    private Reader reader;
    private Worker worker;
    private Book book;
    private BookCopy bookCopy;
    private Rental rental;
    private NewsPost newsPost;
    private AddNewsPostForm addNewsPostForm;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User("John", "Doe", "john.doe@example.com", "1234567890", "password");

        reader = new Reader(user);
        worker = new Worker(5000, user, new EmployeeManager());
        book = new Book("Test Book");
        bookCopy = new BookCopy(book);
        newsPost = new NewsPost("nowy");

        workerRentalForm = new WorkerRentalForm();
        workerRentalForm.setReaderEmail("john.doe@example.com");
        workerRentalForm.setBookId(1);
        workerRentalForm.setWorkerId(1);
        workerRentalForm.setRentalInWeeks(2);

        rental = new Rental(reader, bookCopy, 2);
        rental.setRentalId(1);
        rental.setRentalDate(LocalDate.now().minusWeeks(2));

        addNewsPostForm = new AddNewsPostForm();
        addNewsPostForm.setWorkerId(1);
        addNewsPostForm.setName("News Title");
        addNewsPostForm.setContents("News Contents");
        addNewsPostForm.setImageUrl("http://image.url");
    }

    @Test
    public void testReturnBook_RentalNotFound() {
        // Mock rentalRepository to return empty Optional
        when(rentalRepository.existsById(anyInt())).thenReturn(false);
        // Call the method
        ServiceResponse<Rental> response = workerService.returnBook(new Rental());
        // Verify response
        assertTrue(response.getData().isEmpty());
        assertEquals("Rental Not Found", response.getMessage());
    }

    @Test
    public void testReturnBook_BookCopyNotFound() {
        // Mock rentalRepository to return a rental
        Rental rental = new Rental();
        rental.setRentalId(1);
        BookCopy bookCopy = new BookCopy("5D");
        bookCopy.setCopyId(1);
        rental.setBookCopy(bookCopy);

        when(rentalRepository.existsById(anyInt())).thenReturn(true);
        when(rentalRepository.findById(anyInt())).thenReturn(Optional.of(rental));
        when(bookCopyRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Call the method
        ServiceResponse<Rental> response = workerService.returnBook(rental);

        // Verify response
        assertTrue(response.getData().isEmpty());
        assertEquals("Book Copy Not Found", response.getMessage());
    }

    @Test
    public void testReturnBook_SuccessfulReturn() {
        // Create rental
        Rental rental = new Rental();
        rental.setRentalId(1);
        rental.setRentalDate(LocalDate.now().minusDays(10)); // Set rental date to 10 days ago
        rental.setTimeOfRentalInWeeks(2); // Rental period is 2 weeks

        // Create reader
        Reader reader = new Reader();
        reader.setPenalty(0f); // No penalty initially
        rental.setReader(reader);

        // Create book copy
        BookCopy bookCopy = new BookCopy("6Q");
        bookCopy.setCopyId(1);
        bookCopy.setRentalStatus(RentalStatus.RENTED); // Book is currently rented
        rental.setBookCopy(bookCopy);

        // Mock repositories
        when(rentalRepository.existsById(rental.getRentalId())).thenReturn(true);
        when(rentalRepository.findById(rental.getRentalId())).thenReturn(Optional.of(rental));
        when(bookCopyRepository.findById(bookCopy.getCopyId())).thenReturn(Optional.of(bookCopy));
//        when(readerRepository.save(any(Reader.class))).thenReturn(reader);
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(bookCopy);

        // Call the method
        ServiceResponse<Rental> response = workerService.returnBook(rental);

        // Verify interaction with repositories
        verify(rentalRepository).findById(rental.getRentalId());
        verify(bookCopyRepository).findById(bookCopy.getCopyId());
//        verify(readerRepository).save(reader);

        // Verify response
        assertTrue(response.getData().isPresent());
        assertEquals("Rental saved", response.getMessage());
        assertEquals(RentalStatus.INACTIVE, response.getData().get().getStatus()); // Rental status should be INACTIVE
        assertEquals(0f, response.getData().get().getPenalty()); // Penalty should be 0
        assertEquals(RentalStatus.FREE, bookCopy.getRentalStatus()); // Book copy status should be FREE
        assertEquals(0f, reader.getPenalty()); // Reader's penalty should be 0
    }


    @Test
    public void testReturnBook_SuccessfulReturnPenaltyCounted() {
        // Create rental
        Rental rental = new Rental();
        rental.setRentalId(1);
        rental.setRentalDate(LocalDate.now().minusDays(28)); // Set rental date to 10 days ago
        rental.setTimeOfRentalInWeeks(2); // Rental period is 2 weeks


        // Create reader
        Reader reader = new Reader();
        reader.setPenalty(0f); // No penalty initially
        rental.setReader(reader);

        // Create book copy
        BookCopy bookCopy = new BookCopy("6Q");
        bookCopy.setCopyId(1);
        bookCopy.setRentalStatus(RentalStatus.RENTED); // Book is currently rented
        rental.setBookCopy(bookCopy);

        // Mock repositories
        when(rentalRepository.existsById(rental.getRentalId())).thenReturn(true);
        when(rentalRepository.findById(rental.getRentalId())).thenReturn(Optional.of(rental));
        when(bookCopyRepository.findById(bookCopy.getCopyId())).thenReturn(Optional.of(bookCopy));
        when(readerRepository.save(any(Reader.class))).thenReturn(reader);
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(bookCopy);

        // Call the method
        ServiceResponse<Rental> response = workerService.returnBook(rental);

        // Verify interaction with repositories
        verify(rentalRepository).findById(rental.getRentalId());
        verify(bookCopyRepository).findById(bookCopy.getCopyId());
//        verify(readerRepository).save(reader);

        // Verify response
        assertTrue(response.getData().isPresent());
        assertEquals("Rental saved", response.getMessage());
        assertEquals(RentalStatus.OVERDUE, response.getData().get().getStatus()); // Rental status should be INACTIVE
        assertEquals(2* Consts.OVERDUE_PENALTY_PER_WEEK, response.getData().get().getPenalty()); // Penalty should be 0
        assertEquals(RentalStatus.FREE, bookCopy.getRentalStatus()); // Book copy status should be FREE
        assertEquals(2* Consts.OVERDUE_PENALTY_PER_WEEK, reader.getPenalty()); // Reader's penalty should be 0
    }

    @Test
    void testAddNewsPost_WorkerNotFound() {
        when(workerRepository.findById(addNewsPostForm.getWorkerId())).thenReturn(Optional.empty());

        ServiceResponse<NewsPost> response = workerService.addNewsPost(addNewsPostForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Worker Not Found", response.getMessage());
    }

    @Test
    void testAddNewsPost_Success() {
        when(workerRepository.findById(addNewsPostForm.getWorkerId())).thenReturn(Optional.of(worker));
        when(newsPostRepository.save(any(NewsPost.class))).thenReturn(newsPost);

        ServiceResponse<NewsPost> response = workerService.addNewsPost(addNewsPostForm);

        assertTrue(response.getData().isPresent());
        assertEquals("News Post saved", response.getMessage());
    }

    @Test
    void testUpdateNewsPost_NewsPostNotFound() {
        when(newsPostRepository.findById(addNewsPostForm.getNewsPostId())).thenReturn(Optional.empty());

        ServiceResponse<NewsPost> response = workerService.updateNewsPost(addNewsPostForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("news post not found", response.getMessage());
    }

    @Test
    void testUpdateNewsPost_WorkerNotFound() {
        when(newsPostRepository.findById(addNewsPostForm.getNewsPostId())).thenReturn(Optional.of(newsPost));
        when(workerRepository.findById(addNewsPostForm.getWorkerId())).thenReturn(Optional.empty());

        ServiceResponse<NewsPost> response = workerService.updateNewsPost(addNewsPostForm);

        assertTrue(response.getData().isEmpty());
        assertEquals("Worker Not Found", response.getMessage());
    }

    @Test
    void testUpdateNewsPost_Success() {
        when(newsPostRepository.findById(addNewsPostForm.getNewsPostId())).thenReturn(Optional.of(newsPost));
        when(workerRepository.findById(addNewsPostForm.getWorkerId())).thenReturn(Optional.of(worker));
        when(newsPostRepository.save(any(NewsPost.class))).thenReturn(newsPost);

        ServiceResponse<NewsPost> response = workerService.updateNewsPost(addNewsPostForm);

        assertTrue(response.getData().isPresent());
        assertEquals("News Post Updated", response.getMessage());
    }

    @Test
    void testDeleteNewsPost_NewsPostNotFound() {
        when(newsPostRepository.findById(1)).thenReturn(Optional.empty());

        ServiceResponse<NewsPost> response = workerService.deleteNewsPost(1);

        assertTrue(response.getData().isEmpty());
        assertEquals("News Post Not Found", response.getMessage());
    }

    @Test
    void testDeleteNewsPost_Success() {
        when(newsPostRepository.findById(1)).thenReturn(Optional.of(newsPost));

        ServiceResponse<NewsPost> response = workerService.deleteNewsPost(1);

        assertTrue(response.getData().isPresent());
        assertEquals("News Post deleted", response.getMessage());
    }

    @Test
    public void testBookRental_Success() {
        // Create mock data
        RentalForm rentalForm = new RentalForm(1, 1, 2); // Assuming book ID: 1, reader ID: 1, rental in weeks: 2
        Book book = new Book("Way of Kings");
        Reader reader = new Reader(15f);
        BookCopy rentedCopy = new BookCopy("4F");
        rentedCopy.setRentalStatus(RentalStatus.FREE);
        List<BookCopy> copies = new ArrayList<>();
        copies.add(rentedCopy);
        Rental rental = new Rental(reader, rentedCopy, rentalForm.getRentalInWeeks());
        rental.setStatus(RentalStatus.ACTIVE);

        // Mock repository methods
        when(bookRepository.findById(workerRentalForm.getBookId())).thenReturn(Optional.of(book));
        when(workerRepository.findById(workerRentalForm.getWorkerId())).thenReturn(Optional.of(worker));
        when(readerRepository.findByUserEmail(workerRentalForm.getReaderEmail())).thenReturn(Optional.of(reader));
        when(bookCopyRepository.findBookCopiesByBook(book)).thenReturn(copies);
        when(rentalRepository.save(any())).thenReturn(rental);

        // Call the service method
        ServiceResponse<Rental> response = workerService.rentBookForReader(workerRentalForm);

        // Verify repository method calls
        verify(bookRepository).findById(workerRentalForm.getBookId());
        verify(workerRepository).findById(workerRentalForm.getWorkerId());
        verify(readerRepository).findByUserEmail(workerRentalForm.getReaderEmail());
        verify(bookCopyRepository).findBookCopiesByBook(book);
        verify(rentalRepository).save(any());

        // Check response
        assertEquals("Rental saved", response.getMessage());
        assertEquals(15f, response.getData().get().getReader().getPenalty());
        assertEquals("4F", response.getData().get().getBookCopy().getShelfPlace());
        assertEquals(RentalStatus.ACTIVE, response.getData().get().getStatus());
    }

    @Test
    public void testBookRental_NoFreeCopiesFound(){

        BookCopy rentedCopy = new BookCopy("4F");
        rentedCopy.setRentalStatus(RentalStatus.RENTED);
        List<BookCopy> copies = new ArrayList<>();
        copies.add(rentedCopy);
        Rental rental = new Rental(reader, rentedCopy, workerRentalForm.getRentalInWeeks());
        rental.setStatus(RentalStatus.ACTIVE);

        // Mock repository methods
        when(workerRepository.findById(workerRentalForm.getWorkerId())).thenReturn(Optional.of(worker));
        when(bookRepository.findById(workerRentalForm.getBookId())).thenReturn(Optional.of(book));
        when(readerRepository.findByUserEmail(workerRentalForm.getReaderEmail())).thenReturn(Optional.of(reader));
        when(bookCopyRepository.findBookCopiesByBook(book)).thenReturn(copies);

        ServiceResponse<Rental> response = workerService.rentBookForReader(workerRentalForm);

        assertEquals("No free copies found!", response.getMessage());
        assertEquals(Optional.empty(), response.getData());

    }

    @Test
    public void testBookRental_BookNotFound() {
        // Create mock data
        // Mock repository method to return false indicating book not found
        when(bookRepository.findById(workerRentalForm.getBookId())).thenReturn(Optional.empty());
        when(workerRepository.findById(workerRentalForm.getWorkerId())).thenReturn(Optional.of(worker));
        when(readerRepository.findByUserEmail(workerRentalForm.getReaderEmail())).thenReturn(Optional.of(reader));
        // Call the service method
        ServiceResponse<Rental> response = workerService.rentBookForReader(workerRentalForm);
        // Verify repository method call
        verify(bookRepository).findById(workerRentalForm.getBookId());
        // Check response
        assertEquals("No book found", response.getMessage());
        assertEquals(Optional.empty(), response.getData());
    }

    @Test
    public void testBookRental_ReaderNotFound() {
        // Create mock data
        // Mock repository method to return false indicating book not found
        when(bookRepository.findById(workerRentalForm.getBookId())).thenReturn(Optional.of(book));
        when(workerRepository.findById(workerRentalForm.getWorkerId())).thenReturn(Optional.of(worker));
        when(readerRepository.findByUserEmail(workerRentalForm.getReaderEmail())).thenReturn(Optional.empty());
        // Call the service method
        ServiceResponse<Rental> response = workerService.rentBookForReader(workerRentalForm);
        // Verify repository method call
        verify(readerRepository).findByUserEmail(workerRentalForm.getReaderEmail());
        // Check response
        assertEquals("No reader found", response.getMessage());
        assertEquals(Optional.empty(), response.getData());
    }

    @Test
    public void testBookRentail_badRequest(){
        WorkerRentalForm badFormEmail = new WorkerRentalForm(5, "", 4, 3 );
        ServiceResponse<Rental> response = workerService.rentBookForReader(badFormEmail);
        assertEquals("bad request send", response.getMessage());
        WorkerRentalForm badFormWorkerId = new WorkerRentalForm(5, "afs", -2, 3 );
        response = workerService.rentBookForReader(badFormWorkerId);
        assertEquals("bad request send", response.getMessage());
        WorkerRentalForm badFormBookId = new WorkerRentalForm(-2, "afs", 4, 3 );
        response = workerService.rentBookForReader(badFormBookId);
        assertEquals("bad request send", response.getMessage());
        WorkerRentalForm badFormTime = new WorkerRentalForm(2, "afs", 4, -2);
        response = workerService.rentBookForReader(badFormTime);
        assertEquals("bad request send", response.getMessage());

    }
}