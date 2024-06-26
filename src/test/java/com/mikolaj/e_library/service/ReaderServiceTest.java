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
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ReaderServiceTest {
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

    @InjectMocks
    private ReaderService readerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateUserInfo_ReaderFound() {
        // Create old and new reader objects
        Reader oldReader = new Reader();
        oldReader.setReaderId(1); // Assuming ID is 1
        Reader newReader = new Reader();

        // Mock the repository method to return true when checking if the old reader exists
        when(readerRepository.existsById(oldReader.getReaderId())).thenReturn(true);

        // Mock the repository method to return the updated reader
        when(readerRepository.findById(oldReader.getReaderId())).thenReturn(Optional.of(newReader));

        // Call the service method
        ServiceResponse<Reader> response = readerService.updateUserInfo(oldReader, newReader);

        // Verify that the repository methods were called with the correct arguments
        verify(readerRepository).existsById(oldReader.getReaderId());
        verify(readerRepository).save(newReader);

        // Check the response
        assertEquals("Updated Reader", response.getMessage());
        assertEquals(newReader, response.getData().orElse(null)); // Assuming the updated reader is returned
    }

    @Test
    public void testUpdateUserInfo_ReaderNotFound() {
        // Create old and new reader objects
        Reader oldReader = new Reader();
        oldReader.setReaderId(1); // Assuming ID is 1
        Reader newReader = new Reader();

        // Mock the repository method to return false when checking if the old reader exists
        when(readerRepository.existsById(oldReader.getReaderId())).thenReturn(false);

        // Call the service method
        ServiceResponse<Reader> response = readerService.updateUserInfo(oldReader, newReader);

        // Verify that the repository method was called with the correct argument
        verify(readerRepository).existsById(oldReader.getReaderId());

        // Check the response
        assertEquals("Reader Not Found", response.getMessage());
        assertEquals(Optional.empty(), response.getData()); // Assuming no reader data is returned
    }

    @Test
    public void testBookRental_Success() {
        // Create mock data
        RentalForm rentalForm = new RentalForm("email", 1, 2); // Assuming book ID: 1, reader ID: 1, rental in weeks: 2
        Book book = new Book("Way of Kings");
        Reader reader = new Reader();
        reader.setPenalty(15f);
        reader.setUser(new User("email"));
        BookCopy rentedCopy = new BookCopy("4F");
        rentedCopy.setRentalStatus(RentalStatus.FREE);
        List<BookCopy> copies = new ArrayList<>();
        copies.add(rentedCopy);
        Rental rental = new Rental(reader, rentedCopy, rentalForm.getRentalInWeeks());
        rental.setStatus(RentalStatus.ACTIVE);

        // Mock repository methods
        when(bookRepository.existsById(rentalForm.getBookId())).thenReturn(true);
        when(bookRepository.findById(rentalForm.getBookId())).thenReturn(Optional.of(book));
        when(readerRepository.existsByUserEmail(rentalForm.getReaderEmail())).thenReturn(true);
        when(readerRepository.findByUserEmail(rentalForm.getReaderEmail())).thenReturn(Optional.of(reader));
        when(bookCopyRepository.findBookCopiesByBook(book)).thenReturn(copies);
        when(rentalRepository.save(any())).thenReturn(rental);

        // Call the service method
        ServiceResponse<Rental> response = readerService.bookRental(rentalForm);

        // Verify repository method calls
        verify(bookRepository).existsById(rentalForm.getBookId());
        verify(bookRepository).findById(rentalForm.getBookId());
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
        RentalForm rentalForm = new RentalForm("email", 1, 2); // Assuming book ID: 1, reader ID: 1, rental in weeks: 2
        Book book = new Book("Way of Kings");
        Reader reader = new Reader(15f);
        User user = new User("email");
        reader.setUser(user);
        BookCopy rentedCopy = new BookCopy("4F");
        rentedCopy.setRentalStatus(RentalStatus.RENTED);
        List<BookCopy> copies = new ArrayList<>();
        copies.add(rentedCopy);
        Rental rental = new Rental(reader, rentedCopy, rentalForm.getRentalInWeeks());
        rental.setStatus(RentalStatus.ACTIVE);

        // Mock repository methods
        when(bookRepository.existsById(rentalForm.getBookId())).thenReturn(true);
        when(bookRepository.findById(rentalForm.getBookId())).thenReturn(Optional.of(book));
        when(readerRepository.existsByUserEmail(rentalForm.getReaderEmail())).thenReturn(true);
        when(readerRepository.findByUserEmail(rentalForm.getReaderEmail())).thenReturn(Optional.of(reader));
        when(bookCopyRepository.findBookCopiesByBook(book)).thenReturn(copies);

        ServiceResponse<Rental> response = readerService.bookRental(rentalForm);

        assertEquals("No free copies found!", response.getMessage());
        assertEquals(Optional.empty(), response.getData());

    }

    @Test
    public void testBookRental_BookNotFound() {
        // Create mock data
        RentalForm rentalForm = new RentalForm("email", 1, 2); // Assuming book ID: 1, reader ID: 1, rental in weeks: 2

        // Mock repository method to return false indicating book not found
        when(bookRepository.existsById(rentalForm.getBookId())).thenReturn(false);
        // Call the service method
        ServiceResponse<Rental> response = readerService.bookRental(rentalForm);
        // Verify repository method call
        verify(bookRepository).existsById(rentalForm.getBookId());
        // Check response
        assertEquals("Book Not Found", response.getMessage());
        assertEquals(Optional.empty(), response.getData());
    }

    @Test
    public void testBookRental_ReaderNotFound() {
        // Create mock data
        RentalForm rentalForm = new RentalForm("email", 1, 2); // Assuming book ID: 1, reader ID: 1, rental in weeks: 2
        // Mock repository method to return false indicating book not found
        when(bookRepository.existsById(any())).thenReturn(true);
        when(bookRepository.findById(any())).thenReturn(Optional.of(new Book()));
        when(readerRepository.existsById(any())).thenReturn(false);
        // Call the service method
        ServiceResponse<Rental> response = readerService.bookRental(rentalForm);
        // Verify repository method call
        verify(readerRepository).existsByUserEmail(rentalForm.getReaderEmail());
        verify(bookRepository).existsById(any());
        verify(bookRepository).findById(any());
        // Check response
        assertEquals("Reader Not Found", response.getMessage());
        assertEquals(Optional.empty(), response.getData());
    }

    @Test
    public void testGetFilteredBooks() {
        // Create a list of books
        List<Book> books = new ArrayList<>();
        // Add some books to the list (you can add more if needed)
        books.add(new Book(BookType.HARDOVER, "Way of Kings", LocalDate.now(), BookCategory.DRAMA, "Sanderson", 3.7f));
        books.add(new Book(BookType.PAPERBACK, "Mistborn", LocalDate.now(), BookCategory.DRAMA, "Sanderson",3.2f));
        books.add(new Book(BookType.HARDOVER, "DUne", LocalDate.now(), BookCategory.DRAMA, "Herbert", 2.6f));
        books.add(new Book(BookType.HARDOVER, "Wardbreaker",LocalDate.now(), BookCategory.DRAMA, "Sanderson", 4.6f));

        // Mock the repository method to return the list of books
        when(bookRepository.findByTitleStartingWithIgnoreCase(anyString())).thenReturn(books);
        when(bookRepository.findAll()).thenReturn(books);

        // Create a BookFilter object
        BookFilter bookFilter = new BookFilter(8,0,"","","Sanderson","drama","hardcover",2f);

        // Call the service method
        ServiceResponse<Page<Book>> response = readerService.getFilteredBooks(bookFilter);

        // Check the response
        assertEquals("Filtered List Retrieved", response.getMessage());
        assertEquals(2, response.getData().get().getTotalElements()); // Assuming the size of the filtered list is 3

    }

    @Test
    public void testGetFilteredBooks_filtersByTitle() {
        // Create a list of books
        List<Book> books = new ArrayList<>();
        // Add some books to the list (you can add more if needed)
        books.add(new Book(BookType.HARDOVER, "Way of Kings", LocalDate.now(), BookCategory.DRAMA, "Sanderson", 2.3f));
        books.add(new Book(BookType.PAPERBACK, "Way of Kings", LocalDate.now(), BookCategory.DRAMA, "Sanderson", 4.2f));
        books.add(new Book(BookType.PAPERBACK, "Mistborn", LocalDate.now(), BookCategory.FANTASY, "Sanderson", 1.8f));
        books.add(new Book(BookType.HARDOVER, "DUne", LocalDate.now(), BookCategory.DRAMA, "Herbert", 2.8f));

        // Mock the repository method to return the list of books
        when(bookRepository.findByTitleStartingWithIgnoreCase(anyString())).thenReturn(books.stream().filter(book -> book.getTitle().equals("Way of Kings")).toList());

        // Create a BookFilter object
        BookFilter bookFilter = new BookFilter(8,0,"Way of Kings","","","","",0f);

        // Call the service method
        ServiceResponse<Page<Book>> response = readerService.getFilteredBooks(bookFilter);

        //Verify that the repository method was called with the correct argument
        verify(bookRepository).findByTitleStartingWithIgnoreCase("Way of Kings");

        // Check the response
        assertEquals("Filtered List Retrieved", response.getMessage());
        assertEquals(2, response.getData().get().getTotalElements()); // Assuming the size of the filtered list is 3
    }


    @Test
    public void testGetAllRentalsForReader_NoRentals() {
        // Mocking the repository to return an empty list
        Reader reader = new Reader(new User("email"));
        when(rentalRepository.findAllByReader(any())).thenReturn(Optional.empty());
        when(readerRepository.findByUserEmail(any())).thenReturn(Optional.of(reader));

        // Call the service method
        ServiceResponse<List<Rental>> response = readerService.getAllRentalsForReader(reader.getUser().getEmail());

        // Verify the response
        assertEquals("No Rental Found", response.getMessage());
        assertEquals(Optional.empty(), response.getData());
    }

    @Test
    public void testGetAllRentalsForReader_RentalsFound() {
        // Mocking the repository to return a list of rentals
        List<Rental> rentals = new ArrayList<>();
        Reader reader = new Reader(3f);
        reader.setUser(new User("email"));
        reader.setReaderId(1);
        Rental rental1 = new Rental(5f, RentalStatus.INACTIVE,reader);
        Rental rental2 = new Rental(12f, RentalStatus.INACTIVE,reader);
        rentals.add(rental1);
        rentals.add(rental2);

        when(rentalRepository.findAllByReader(any())).thenReturn(Optional.of(rentals));
        when(readerRepository.findByUserEmail(any())).thenReturn(Optional.of(reader));
        // Call the service method
        ServiceResponse<List<Rental>> response = readerService.getAllRentalsForReader(reader.getUser().getEmail());

        // Verify the response
        assertEquals("Rentals found", response.getMessage());
        assertEquals(rentals, response.getData().get());
    }

    @Test
    public void testGetAllActiveRentalsForReader_NoActiveRentals() {
        // Mocking the repository to return an empty list
        when(rentalRepository.findAllByReader(any())).thenReturn(Optional.empty());
        Reader reader = new Reader(3f);
        reader.setUser(new User("email"));

        when(readerRepository.findByUserEmail(any())).thenReturn(Optional.of(reader));
        // Call the service method
        ServiceResponse<List<Rental>> response = readerService.getAllActiveRentalsForReader(reader.getUser().getEmail());

        // Verify the response
        assertEquals("No active Rental Found", response.getMessage());
        assertEquals(Optional.empty(), response.getData());
    }

    @Test
    public void testGetAllActiveRentalsForReader_ActiveRentalsFound() {
        // Mocking the repository to return a list of rentals with some active rentals
        List<Rental> rentals = new ArrayList<>();
        Reader reader = new Reader(3f);
        reader.setUser(new User("email"));
        reader.setReaderId(1);
        // Add some rentals with status ACTIVE
        Rental rental1 = new Rental(5f, RentalStatus.ACTIVE, reader);
        Rental rental2 = new Rental(12f, RentalStatus.ACTIVE, reader);
        Rental rental3 = new Rental(7f, RentalStatus.INACTIVE, reader);
        rentals.add(rental1);
        rentals.add(rental2);
        rentals.add(rental3);

        when(rentalRepository.findAllByReader(any())).thenReturn(Optional.of(rentals));
        when(readerRepository.findByUserEmail(any())).thenReturn(Optional.of(reader));

        // Call the service method
        ServiceResponse<List<Rental>> response = readerService.getAllActiveRentalsForReader(reader.getUser().getEmail());

        // Verify the response
        assertEquals("Active rentals found", response.getMessage());
        assertEquals(2, response.getData().get().size()); // Assuming 2 active rentals in the mocked list
    }

    @Test
    public void testProlongateRental_RentalNotFound() {
        // Mocking the repository to return null when finding rental by ID
        when(rentalRepository.findById(any())).thenReturn(Optional.empty());

        // Call the service method
        ServiceResponse<Rental> response = readerService.prolongateRental(new ProlongateForm(1, 3));

        // Verify the response
        assertEquals("Rental Not Found", response.getMessage());
        assertEquals(Optional.empty(), response.getData());
    }

    @Test
    public void testProlongateRental_RentalProlonged() {
        // Mocking the repository to return a rental when finding by ID
        Reader reader = new Reader(15f);
        reader.setReaderId(1);
        BookCopy bookCopy = new BookCopy("4D");
        Rental rental = new Rental(reader, bookCopy, 4);
        rental.setRentalId(1);
        when(rentalRepository.findById(any())).thenReturn(Optional.of(rental));
        // Mocking the reader repository
        when(readerRepository.save(reader)).thenReturn(reader);

        // Call the service method
        ServiceResponse<Rental> response = readerService.prolongateRental(new ProlongateForm(1, 3));

        // Verify the response
        assertEquals("Rental prolonged", response.getMessage());
        Rental responseData = response.getData().get();
        assertTrue(responseData.isProlonged());
        assertEquals(responseData.getStatus(), RentalStatus.PROLONGED);
        assertEquals(responseData.getPenalty(), 3*Consts.PROLONGATION_PENALTY_PER_WEEK);
    }

    @Test
    public void testAddOrUpdateBookRating_BookNotFound() {
        // Mocking the repository to return false when checking if book exists
        when(bookRepository.existsById(any())).thenReturn(false);

        // Call the service method
        ServiceResponse<BookRating> response = readerService.addOrUpdateBookRating(new BookRatingForm());

        // Verify the response
        assertEquals("Book not found", response.getMessage());
        assertEquals(Optional.empty(), response.getData());
    }

    @Test
    public void testAddOrUpdateBookRating_ReaderNotFound() {
        // Mocking the repository to return true for book existence check
        when(bookRepository.existsById(any())).thenReturn(true);
        // Mocking the repository to return false when checking if reader exists
        when(readerRepository.existsById(any())).thenReturn(false);

        // Call the service method
        ServiceResponse<BookRating> response = readerService.addOrUpdateBookRating(new BookRatingForm());

        // Verify the response
        assertEquals("Reader not found", response.getMessage());
        assertEquals(Optional.empty(), response.getData());
    }

    @Test
    public void testAddOrUpdateBookRating_BookRatingAdded() {
        Book book = new Book("book1");
        book.setBookId(1);
        Reader reader = new Reader(4f);
        reader.setReaderId(1);
        Reader reader1 = new Reader(3f);
        reader1.setReaderId(2);
        // Mocking the repository to return true for book and reader existence check
        when(bookRepository.existsById(any())).thenReturn(true);
        when(readerRepository.existsById(any())).thenReturn(true);

        // Mocking the repository to return empty optional for existing book rating
        when(bookRatingRepository.findByBookAndReader(book,reader)).thenReturn(Optional.empty());

        // Mocking the repository to return a book rating when saving
        BookRating mockBookRating = new BookRating(4, book, reader);
        when(bookRatingRepository.save(mockBookRating)).thenReturn(mockBookRating);

        // Mocking the repository to return a list of book ratings for the book
        List<BookRating> listOfRatings = new ArrayList<>();
        // Add mock ratings to the list (you can add more if needed)
        listOfRatings.add(new BookRating(4, book, reader));
        // Mocking the repository method to return the list of ratings
        when(bookRatingRepository.getBookRatingsByBook(book)).thenReturn(listOfRatings);
        when(readerRepository.findById(1)).thenReturn(Optional.of(reader));
        when(readerRepository.findById(2)).thenReturn(Optional.of(reader1));
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));

        // Mocking the repository to return the book when saving
        Book mockBook = new Book("book2");
        when(bookRepository.save(mockBook)).thenReturn(mockBook);

        // Call the service method
        ServiceResponse<BookRating> response = readerService.addOrUpdateBookRating(new BookRatingForm(4, 1,1));

        // Verify the response
        assertEquals("Book rating updated", response.getMessage());
        BookRating responseBookRating = response.getData().get();
        assertEquals(4f, responseBookRating.getRating());
        assertEquals(book, responseBookRating.getBook());
        assertEquals(reader, responseBookRating.getReader());
        //updating the rating for the same reader and book
        when(bookRatingRepository.findByBookAndReader(book,reader)).thenReturn(Optional.of(responseBookRating));
        ServiceResponse<BookRating> response1 = readerService.addOrUpdateBookRating(new BookRatingForm(3,1,1));
        assertEquals("Book rating updated", response1.getMessage());
        BookRating responseBookRating1 = response1.getData().get();
        assertEquals(3f, responseBookRating1.getRating());
    }

    @Test
    public void testGetNewsPostsBetweenDates_NoDateRange() {
        // Mocking the repository to return all news posts
        List<NewsPost> allNewsPosts = new ArrayList<>();
        // Add mock news posts to the list (you can add more if needed)
        allNewsPosts.add(new NewsPost("new Post"));
        when(newsPostRepository.findAll()).thenReturn(allNewsPosts);

        // Call the service method without a date range
        ServiceResponse<List<NewsPost>> response = readerService.getNewsPostsBetweenDates(null);

        // Verify the response
        assertEquals("News Posts Found", response.getMessage());
        assertEquals(allNewsPosts, response.getData().get());
    }

    @Test
    public void testGetNewsPostsBetweenDates_WithDateRange() {
        // Mocking the repository to return news posts within the date range
        LocalDate startDate = LocalDate.now().minusDays(16);
        LocalDate endDate = LocalDate.now();
        List<NewsPost> newsPostsInRange = new ArrayList<>();
        // Add mock news posts within the range to the list (you can add more if needed)
        newsPostsInRange.add(new NewsPost("newPost"));
        when(newsPostRepository.findNewsPostsByCreateTimeBetween(any(), any())).thenReturn(newsPostsInRange);

        // Create a DateRange object with the start and end dates
        DateRange dateRange = new DateRange(startDate, endDate);

        // Call the service method with the date range
        ServiceResponse<List<NewsPost>> response = readerService.getNewsPostsBetweenDates(dateRange);

        // Verify the response
        assertEquals("News Posts Found", response.getMessage());
        assertEquals(newsPostsInRange, response.getData().get());
    }

    @Test
    public void testGetNewsPostsBetweenDates_NoPostsFound() {
        // Mocking the repository to return an empty list of news posts
        when(newsPostRepository.findAll()).thenReturn(new ArrayList<>());

        // Call the service method without a date range
        ServiceResponse<List<NewsPost>> response = readerService.getNewsPostsBetweenDates(null);

        // Verify the response
        assertEquals("No News Posts Found", response.getMessage());
        assertEquals(Optional.empty(), response.getData());
    }

    @Test
    public void test_successfully_reserve_free_book_copy() {
        int bookId = 1;
        int readerId = 1;
        Reader reader = new Reader();
        Book book = new Book();
        BookCopy bookCopy = new BookCopy();
        bookCopy.setRentalStatus(RentalStatus.FREE);
        List<BookCopy> copies = Collections.singletonList(bookCopy);

        when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookCopyRepository.findBookCopiesByBook(book)).thenReturn(copies);

        ServiceResponse<BookCopy> response = readerService.reserveBook(bookId, readerId);

        verify(bookCopyRepository).save(bookCopy);
        assertEquals("Book copy reserved", response.getMessage());
        assertEquals(RentalStatus.RESERVED, response.getData().get().getRentalStatus());
    }

    @Test
    public void test_rental_status_updated_to_reserved() {
        int bookId = 1;
        int readerId = 1;
        Reader reader = new Reader();
        Book book = new Book();
        BookCopy bookCopy = new BookCopy();
        bookCopy.setRentalStatus(RentalStatus.FREE);
        List<BookCopy> copies = Collections.singletonList(bookCopy);

        when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookCopyRepository.findBookCopiesByBook(book)).thenReturn(copies);

        ServiceResponse<BookCopy> response = readerService.reserveBook(bookId, readerId);

        assertEquals(RentalStatus.RESERVED, response.getData().get().getRentalStatus());
    }

    @Test
    public void test_reserve_book_with_invalid_book_id() {
        int invalidBookId = 999;
        int readerId = 1;
        Reader reader = new Reader(24f);
        when(bookRepository.findById(invalidBookId)).thenReturn(Optional.empty());
        when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));

        ServiceResponse<BookCopy> response = readerService.reserveBook(invalidBookId, readerId);

        assertEquals("Book not found", response.getMessage());
    }

    @Test
    public void test_reserve_book_with_invalid_reader_id() {
        int bookId = 1;
        int invalidReaderId = 999;

        when(readerRepository.findById(invalidReaderId)).thenReturn(Optional.empty());

        ServiceResponse<BookCopy> response = readerService.reserveBook(bookId, invalidReaderId);

        assertEquals("Reader not found", response.getMessage());
    }

    @Test
    public void test_reserve_book_no_free_copies_available() {
        int bookId = 1;
        int readerId = 1;
        Reader reader = new Reader();
        Book book = new Book();
        BookCopy bookCopy = new BookCopy();
        bookCopy.setRentalStatus(RentalStatus.RENTED);
        List<BookCopy> copies = Collections.singletonList(bookCopy);

        when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookCopyRepository.findBookCopiesByBook(book)).thenReturn(copies);

        ServiceResponse<BookCopy> response = readerService.reserveBook(bookId, readerId);

        assertEquals("No free book copies found", response.getMessage());
    }

        // Returns reserved book copies when reader exists and has reserved copies
        @Test
        public void test_returns_reserved_copies_when_reader_exists_and_has_reserved_copies() {
            int readerId = 1;
            Reader reader = new Reader();
            reader.setReaderId(readerId);
            List<BookCopy> reservedCopies = Arrays.asList(new BookCopy(), new BookCopy());

            when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
            when(bookCopyRepository.findBookCopiesByReaderAndRentalStatus(reader, RentalStatus.RESERVED)).thenReturn(reservedCopies);

            ServiceResponse<List<BookCopy>> response = readerService.getReservedCopiesForReader(readerId);

            assertTrue(response.getData().isPresent());
            assertEquals(reservedCopies, response.getData().get());
            assertEquals("ReservedCopiesFound", response.getMessage());
        }

        // Returns an empty list when reader exists but has no reserved copies
        @Test
        public void test_returns_empty_list_when_reader_exists_but_has_no_reserved_copies() {
            int readerId = 1;
            Reader reader = new Reader();
            reader.setReaderId(readerId);
            List<BookCopy> reservedCopies = Collections.emptyList();

            when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
            when(bookCopyRepository.findBookCopiesByReaderAndRentalStatus(reader, RentalStatus.RESERVED)).thenReturn(reservedCopies);

            ServiceResponse<List<BookCopy>> response = readerService.getReservedCopiesForReader(readerId);

            assertTrue(response.getData().isPresent());
            assertTrue(response.getData().get().isEmpty());
            assertEquals("ReservedCopiesFound", response.getMessage());
        }

        // Correctly identifies and returns reserved copies for a given reader ID
        @Test
        public void test_correctly_identifies_and_returns_reserved_copies_for_given_reader_id() {
            int readerId = 1;
            Reader reader = new Reader();
            reader.setReaderId(readerId);
            List<BookCopy> reservedCopies = Arrays.asList(new BookCopy(), new BookCopy());

            when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
            when(bookCopyRepository.findBookCopiesByReaderAndRentalStatus(reader, RentalStatus.RESERVED)).thenReturn(reservedCopies);

            ServiceResponse<List<BookCopy>> response = readerService.getReservedCopiesForReader(readerId);

            assertTrue(response.getData().isPresent());
            assertEquals(reservedCopies, response.getData().get());
            assertEquals("ReservedCopiesFound", response.getMessage());
        }

        // Properly interacts with the repository to fetch reserved copies
        @Test
        public void test_properly_interacts_with_repository_to_fetch_reserved_copies() {
            int readerId = 1;
            Reader reader = new Reader();
            reader.setReaderId(readerId);
            List<BookCopy> reservedCopies = Arrays.asList(new BookCopy(), new BookCopy());

            when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
            when(bookCopyRepository.findBookCopiesByReaderAndRentalStatus(reader, RentalStatus.RESERVED)).thenReturn(reservedCopies);

            ServiceResponse<List<BookCopy>> response = readerService.getReservedCopiesForReader(readerId);

            verify(readerRepository).findById(readerId);
            verify(bookCopyRepository).findBookCopiesByReaderAndRentalStatus(reader, RentalStatus.RESERVED);

            assertTrue(response.getData().isPresent());
            assertEquals(reservedCopies, response.getData().get());
            assertEquals("ReservedCopiesFound", response.getMessage());
        }

        // Reader ID does not exist in the repository
        @Test
        public void test_reader_id_does_not_exist_in_repository() {
            int readerId = 1;

            when(readerRepository.findById(readerId)).thenReturn(Optional.empty());

            ServiceResponse<List<BookCopy>> response = readerService.getReservedCopiesForReader(readerId);

            assertFalse(response.getData().isPresent());
            assertEquals("Reader not found", response.getMessage());
        }

        // Reader ID is null or invalid
        @Test
        public void test_reader_id_is_null_or_invalid() {
            Integer readerId = 0;

            ServiceResponse<List<BookCopy>> response = readerService.getReservedCopiesForReader(readerId);

            assertFalse(response.getData().isPresent());
            assertEquals("reader id cannot be 0", response.getMessage());
        }

        // Reader exists but has no book copies associated with them
        @Test
        public void test_reader_exists_but_has_no_book_copies_associated_with_them() {
            int readerId = 1;
            Reader reader = new Reader();
            reader.setReaderId(readerId);
            List<BookCopy> reservedCopies = Collections.emptyList();

            when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
            when(bookCopyRepository.findBookCopiesByReaderAndRentalStatus(reader, RentalStatus.RESERVED)).thenReturn(reservedCopies);

            ServiceResponse<List<BookCopy>> response = readerService.getReservedCopiesForReader(readerId);

            assertTrue(response.getData().isPresent());
            assertTrue(response.getData().get().isEmpty());
            assertEquals("ReservedCopiesFound", response.getMessage());
        }




}
