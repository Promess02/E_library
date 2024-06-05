package com.mikolaj.e_library.service_gen;

import com.mikolaj.e_library.DTO.*;
import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.BookCopy;
import com.mikolaj.e_library.model.Reader;
import com.mikolaj.e_library.model.Rental;
import com.mikolaj.e_library.repo.*;
import com.mikolaj.e_library.service.ReaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReaderServiceTest_Gen {
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
    public void test_update_user_info_when_reader_exists() {
        Reader oldReader = new Reader();
        oldReader.setReaderId(1);
        Reader newReader = new Reader();

        when(readerRepository.existsById(oldReader.getReaderId())).thenReturn(true);
        when(readerRepository.findById(oldReader.getReaderId())).thenReturn(Optional.of(newReader));

        ServiceResponse<Reader> response = readerService.updateUserInfo(oldReader, newReader);

        verify(readerRepository).existsById(oldReader.getReaderId());
        verify(readerRepository).save(newReader);

        assertEquals("Updated Reader", response.getMessage());
        assertEquals(newReader, response.getData().orElse(null));
    }

    @Test
    public void test_book_rental_when_book_and_reader_exist_and_copy_is_available() {
        RentalForm rentalForm = new RentalForm(1, 1, 2);
        Book book = new Book();
        book.setBookId(1);
        Reader reader = new Reader();
        reader.setReaderId(1);
        BookCopy bookCopy = new BookCopy();
        bookCopy.setRentalStatus(RentalStatus.FREE);
        List<BookCopy> copies = Collections.singletonList(bookCopy);

        when(bookRepository.existsById(rentalForm.getBookId())).thenReturn(true);
        when(bookRepository.findById(rentalForm.getBookId())).thenReturn(Optional.of(book));
        when(readerRepository.existsById(rentalForm.getReaderId())).thenReturn(true);
        when(readerRepository.findById(rentalForm.getReaderId())).thenReturn(Optional.of(reader));
        when(bookCopyRepository.findBookCopiesByBook(book)).thenReturn(copies);

        ServiceResponse<Rental> response = readerService.bookRental(rentalForm);

        verify(bookCopyRepository).save(bookCopy);
        //verify(rentalRepository).save(any(Rental.class));

        assertEquals("Rental saved", response.getMessage());
        assertTrue(response.getData().isPresent());
    }

    @Test
    public void test_get_all_rentals_for_reader_when_rentals_exist() {
        Reader reader = new Reader();
        List<Rental> rentals = Arrays.asList(new Rental(), new Rental());

        when(rentalRepository.findAllByReader(reader)).thenReturn(Optional.of(rentals));

        ServiceResponse<List<Rental>> response = readerService.getAllRentalsForReader(reader);

        assertEquals("Rentals found", response.getMessage());
        assertTrue(response.getData().isPresent());
        assertEquals(rentals, response.getData().get());
    }

    @Test
    public void test_prolongate_rental_when_rental_exists() {
        ProlongateForm prolongateForm = new ProlongateForm(1, 2);
        Rental rental = new Rental();
        rental.setRentalId(1);
        rental.setPenalty(0f);
        rental.setTimeOfRentalInWeeks(2);
        Reader reader = new Reader();
        reader.setPenalty(0f);
        rental.setReader(reader);

        when(rentalRepository.findById(prolongateForm.getRentalId())).thenReturn(Optional.of(rental));

        ServiceResponse<Rental> response = readerService.prolongateRental(prolongateForm);

        verify(readerRepository).save(reader);
        verify(rentalRepository).save(rental);

        assertEquals("Rental prolonged", response.getMessage());
        assertTrue(response.getData().isPresent());
    }

    @Test
    public void test_get_filtered_books_based_on_various_filters() {
        BookFilter bookFilter = new BookFilter(10, 0, null, null, "author", "category", "type", 4.0f);
        Book book = new Book(BookType.HARDOVER,"Way of kings", LocalDate.now().minusDays(26),
                BookCategory.DRAMA,"sanderson", "new desc","nowy autor");
        List<Book> books = Arrays.asList(book, book);


        when(bookRepository.findAll()).thenReturn(books);

        ServiceResponse<Page<Book>> response = readerService.getFilteredBooks(bookFilter);

        assertEquals("Filtered List Retrieved", response.getMessage());
        assertTrue(response.getData().isPresent());
    }

    @Test
    public void test_update_user_info_when_reader_does_not_exist() {
        Reader oldReader = new Reader();
        oldReader.setReaderId(1);
        Reader newReader = new Reader();

        when(readerRepository.existsById(oldReader.getReaderId())).thenReturn(false);

        ServiceResponse<Reader> response = readerService.updateUserInfo(oldReader, newReader);

        assertEquals("Reader Not Found", response.getMessage());
        assertFalse(response.getData().isPresent());
    }

    @Test
    public void test_book_rental_when_book_does_not_exist() {
        RentalForm rentalForm = new RentalForm(1, 1, 2);

        when(bookRepository.existsById(rentalForm.getBookId())).thenReturn(false);

        ServiceResponse<Rental> response = readerService.bookRental(rentalForm);

        assertEquals("Book Not Found", response.getMessage());
        assertFalse(response.getData().isPresent());
    }

    @Test
    public void test_book_rental_when_reader_does_not_exist() {
        RentalForm rentalForm = new RentalForm(1, 1, 2);

        when(bookRepository.existsById(rentalForm.getBookId())).thenReturn(true);
        when(readerRepository.existsById(rentalForm.getReaderId())).thenReturn(false);

        ServiceResponse<Rental> response = readerService.bookRental(rentalForm);

        assertEquals("Reader Not Found", response.getMessage());
        assertFalse(response.getData().isPresent());
    }

    @Test
    public void test_book_rental_when_no_free_copies_are_available() {
        RentalForm rentalForm = new RentalForm(1, 1, 2);
        Book book = new Book();

        when(bookRepository.existsById(rentalForm.getBookId())).thenReturn(true);
        when(bookRepository.findById(rentalForm.getBookId())).thenReturn(Optional.of(book));

        List<BookCopy> copies = Collections.emptyList();

        when(bookCopyRepository.findBookCopiesByBook(book)).thenReturn(copies);

        ServiceResponse<Rental> response = readerService.bookRental(rentalForm);

        assertEquals("Book Copies Not Found", response.getMessage());
        assertFalse(response.getData().isPresent());
    }

}
