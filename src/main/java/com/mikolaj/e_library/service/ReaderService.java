package com.mikolaj.e_library.service;

import com.mikolaj.e_library.Consts;
import com.mikolaj.e_library.DTO.*;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.repo.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ReaderService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final NewsPostRepository newsPostRepository;
    private final RentalRepository rentalRepository;
    private final BookRatingRepository bookRatingRepository ;
    private final BookCopyRepository bookCopyRepository;
    private final ReaderRepository readerRepository;

    public ReaderService(BookRepository bookRepository,
                         UserRepository userRepository, RentalRepository rentalRepository,
                         BookRatingRepository bookRatingRepository,
                         BookCopyRepository bookCopyRepository, ReaderRepository readerRepository,
                         NewsPostRepository newsPostRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.readerRepository = readerRepository;
        this.newsPostRepository = newsPostRepository;
    }

    public ServiceResponse<Reader> updateUserInfo(Reader oldReader, Reader newReader){
        if(!readerRepository.existsById(oldReader.getReaderId())){
            return new ServiceResponse<>(Optional.empty(), "Reader Not Found");
        }
        readerRepository.save(newReader);
        return new ServiceResponse<>(
                readerRepository.findById(oldReader.getReaderId()), "Updated Reader");
    }

    public ServiceResponse<Rental> bookRental(RentalForm rentalForm){
        Optional<Book> book;
        Optional<Reader> reader;

        if(!bookRepository.existsById(rentalForm.getBookId())) return new ServiceResponse<>(Optional.empty(), "Book Not Found");
        else book = bookRepository.findById(rentalForm.getBookId());

        if(!readerRepository.existsByUserEmail(rentalForm.getReaderEmail())) return new ServiceResponse<>(Optional.empty(), "Reader Not Found");
        else reader = readerRepository.findByUserEmail(rentalForm.getReaderEmail());


        List<BookCopy> copies = bookCopyRepository.findBookCopiesByBook(book.get());
        if(copies.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Book Copies Not Found");


        BookCopy rentedCopy = copies.stream()
                .filter(copy -> copy.getRentalStatus().equals(RentalStatus.FREE))
                .findFirst()
                .orElse(null);

        if(rentedCopy==null)
            return new ServiceResponse<>(Optional.empty(), "No free copies found!");

        rentedCopy.setRentalStatus(RentalStatus.RENTED);
        rentedCopy.setReader(reader.get());
        bookCopyRepository.save(rentedCopy);
        Rental rental = new Rental(reader.get(), rentedCopy, rentalForm.getRentalInWeeks());
        rental.setStatus(RentalStatus.ACTIVE);
        rentalRepository.save(rental);

        return new ServiceResponse<>(Optional.of(rental), "Rental saved");
    }

    public ServiceResponse<List<Rental>> getAllRentalsForReader(String email){
        Optional<Reader> reader = readerRepository.findByUserEmail(email);
        if(reader.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Reader Not Found");
        Optional<List<Rental>> list = rentalRepository.findAllByReader(reader.get());
        if(list.isEmpty()) return new ServiceResponse<>(Optional.empty(), "No Rental Found");
        return new ServiceResponse<>(list, "Rentals found");
    }

    public ServiceResponse<List<Rental>> getAllActiveRentalsForReader(String email){
        Optional<Reader> reader = readerRepository.findByUserEmail(email);
        if(reader.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Reader Not Found");
        Optional<List<Rental>> list = rentalRepository.findAllByReader(reader.get());
        if(list.isEmpty()) return new ServiceResponse<>(Optional.empty(), "No active Rental Found");
        List<Rental> mathcingList = list.get().stream().filter(rental -> rental.getStatus().equals(RentalStatus.ACTIVE)).toList();
        return new ServiceResponse<>(Optional.of(mathcingList), "Active rentals found");
    }

    @Transactional
    public ServiceResponse<Rental> prolongateRental(ProlongateForm prolongateForm){
        int prolongationInWeeks = prolongateForm.getProlongationInWeeks();
        Optional<Rental> rentalDb = rentalRepository.findById(prolongateForm.getRentalId());
        if(rentalDb.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Rental Not Found");
        Rental rental = rentalDb.get();

        rental.setProlonged(true);
        rental.setTimeOfRentalInWeeks(rental.getTimeOfRentalInWeeks()+prolongationInWeeks);
        Float penaltyAdded = rental.getPenalty()+(prolongationInWeeks* Consts.PROLONGATION_PENALTY_PER_WEEK);
        rental.setPenalty(penaltyAdded);
        rental.getReader().setPenalty(rental.getReader().getPenalty()+penaltyAdded);
        rental.setStatus(RentalStatus.PROLONGED);

        readerRepository.save(rental.getReader());
        rentalRepository.save(rental);

        return new ServiceResponse<>(Optional.of(rental), "Rental prolonged");
    }

    public ServiceResponse<Page<Book>> getFilteredBooks(BookFilter bookFilter) {
        List<Book> filteredList;
        List<Book> allBooks = bookRepository.findAll();
        filteredList = applyFilters(allBooks, bookFilter);

        if(bookFilter.getFilter()!=null)
            if (!bookFilter.getFilter().isEmpty()) {
                filteredList = bookRepository.findByTitleStartingWithIgnoreCase(bookFilter.getFilter());
            }

        // Determine pagination parameters
        int start = bookFilter.getPage() * bookFilter.getSize();
        int end = Math.min(start + bookFilter.getSize(), filteredList.size());
        PageRequest pageable = PageRequest.of(bookFilter.getPage(), bookFilter.getSize());

        // Create a Page instance
        Page<Book> pageResult = new PageImpl<>(filteredList.subList(start, end), pageable, filteredList.size());

        return new ServiceResponse<>(Optional.of(pageResult), "Filtered List Retrieved");
    }

    private List<Book> applyFilters(List<Book> books, BookFilter bookFilter) {
        List<Book> filteredList = books;

        if (bookFilter.getAuthor()!=null && !bookFilter.getAuthor().isEmpty()) {
            filteredList = filteredList.stream()
                    .filter(book -> book.getBookAuthor().equals(bookFilter.getAuthor()))
                    .toList();
        }
        if (bookFilter.getBookCategory()!=null && !bookFilter.getBookCategory().isEmpty()) {
            filteredList = filteredList.stream()
                    .filter(book -> book.getBookCategory().toString().equals(bookFilter.getBookCategory()))
                    .toList();
        }
        if (bookFilter.getBookType()!=null && !bookFilter.getBookType().isEmpty()) {
            filteredList = filteredList.stream()
                    .filter(book -> book.getBookType().toString().equals(bookFilter.getBookType()))
                    .toList();
        }
        if (bookFilter.getMinBookRating()!=null && bookFilter.getMinBookRating() != 0) {
            filteredList = filteredList.stream()
                    .filter(book -> book.getAverageBookRating() >= bookFilter.getMinBookRating())
                    .toList();
        }
        return filteredList;
    }

    public ServiceResponse<BookCopy> reserveBook(int bookId, int readerId){
        Optional<Reader> reader = readerRepository.findById(readerId);
        if(reader.isEmpty()) return new ServiceResponse<>(Optional.empty(),"Reader not found");
        Optional<Book> book = bookRepository.findById(bookId);
        if(book.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Book not found");
        List<BookCopy> copies = bookCopyRepository.findBookCopiesByBook(book.get());
        BookCopy reservedCopy = new BookCopy();
        for(BookCopy copy: copies){
            if(copy.getRentalStatus().equals(RentalStatus.FREE)){
                copy.setRentalStatus(RentalStatus.RESERVED);
                copy.setReader(reader.get());
                reservedCopy = copy;
                break;
            }
        }
        if(!reservedCopy.getRentalStatus().equals(RentalStatus.RESERVED))
            return new ServiceResponse<>(Optional.empty(), "No free book copies found");
        bookCopyRepository.save(reservedCopy);
        return new ServiceResponse<>(Optional.of(reservedCopy), "Book copy reserved");
    }

    public ServiceResponse<List<BookCopy>> getReservedCopiesForReader(int readerId){
        if(readerId==0) return new ServiceResponse<>(Optional.empty(),"reader id cannot be 0");
        Optional<Reader> reader = readerRepository.findById(readerId);
        if(reader.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Reader not found");
        List<BookCopy> copies = bookCopyRepository.findBookCopiesByReaderAndRentalStatus(reader.get(), RentalStatus.RESERVED);
        return new ServiceResponse<>(Optional.of(copies), "ReservedCopiesFound");
    }

    public ServiceResponse<List<NewsPost>> getNewsPostsBetweenDates(DateRange dateRange){
        List<NewsPost> list;
        if(dateRange!=null){
            LocalDateTime startTime = dateRange.getStartDate().atTime(0,0,0);
            LocalDateTime endTime = dateRange.getEndDate().atTime(23, 59, 59);
            list = newsPostRepository.findNewsPostsByCreateTimeBetween(startTime,endTime);
        }
        else list = newsPostRepository.findAll();
        if(list.isEmpty()) return new ServiceResponse<>(Optional.empty(), "No News Posts Found");
        return new ServiceResponse<>(Optional.of(list), "News Posts Found");
    }

    public ServiceResponse<BookRating> addOrUpdateBookRating(BookRatingForm bookRatingForm){
        if(!bookRepository.existsById(bookRatingForm.getBookId())){
            return new ServiceResponse<>(Optional.empty(),"Book not found");
        }
        if(!readerRepository.existsById(bookRatingForm.getReaderId())){
            return new ServiceResponse<>(Optional.empty(),"Reader not found");
        }
        Reader reader = readerRepository.findById(bookRatingForm.getReaderId()).get();
        Book book = bookRepository.findById(bookRatingForm.getBookId()).get();

        Optional<BookRating> dbBookRating = bookRatingRepository.findByBookAndReader(book, reader);
        BookRating returnValue;
        if(dbBookRating.isPresent()){
            dbBookRating.get().setRating(bookRatingForm.getRating());
            bookRatingRepository.save(dbBookRating.get());
            returnValue = dbBookRating.get();
        }else{
            BookRating bookRating = new BookRating(bookRatingForm.getRating(), book, reader);
            bookRatingRepository.save(bookRating);
            returnValue = bookRating;
        }

        List<BookRating> listOfRatings = bookRatingRepository.getBookRatingsByBook(book);
        int sumRatings = 0;
        for(BookRating bookRatings : listOfRatings){
            sumRatings+=bookRatings.getRating();
        }
        Float averageRating = (float) Math.round((float) sumRatings /listOfRatings.size());
        book.setAverageBookRating(averageRating);
        bookRepository.save(book);

        return new ServiceResponse<>(Optional.of(returnValue), "Book rating updated");
    }

    @Transactional
    public ServiceResponse<BookCopy> cancelReservation(int bookId, int readerId) {
        Optional<Reader> reader = readerRepository.findById(readerId);
        if(reader.isEmpty()) return new ServiceResponse<>(Optional.empty(),"Reader not found");
        Optional<Book> book = bookRepository.findById(bookId);
        if(book.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Book not found");
        List<BookCopy> reservedCopyList = bookCopyRepository.findBookCopyByBookAndReader(book.get(),reader.get());
        if(reservedCopyList.isEmpty()) return new ServiceResponse<>(Optional.empty(), "No reservation found");
        BookCopy reservedCopy = reservedCopyList.get(0);
        reservedCopy.setRentalStatus(RentalStatus.FREE);
        reservedCopy.setReader(null);
        bookCopyRepository.save(reservedCopy);
        return new ServiceResponse<>(Optional.of(reservedCopy), "Reservation canceled");
    }
}
