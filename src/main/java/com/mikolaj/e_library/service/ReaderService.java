package com.mikolaj.e_library.service;

import com.mikolaj.e_library.Consts;
import com.mikolaj.e_library.DTO.*;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.repo.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        return new ServiceResponse<>(Optional.of(
                readerRepository.getById(oldReader.getReaderId())), "Updated Reader");
    }

    public ServiceResponse<Rental> bookRental(RentalForm rentalForm){

        Optional<Book> book;
        Optional<Reader> reader;

        if(!bookRepository.existsById(rentalForm.getBookId())){
            return new ServiceResponse<>(Optional.empty(), "Book Not Found");
        }else{
            book = bookRepository.findById(rentalForm.getBookId());
        }

        if(!readerRepository.existsById(rentalForm.getReaderId())){
            return new ServiceResponse<>(Optional.empty(), "Reader Not Found");
        }else{
            reader = readerRepository.findById(rentalForm.getReaderId());
        }

        List<BookCopy> copies = bookCopyRepository.findBookCopiesByBook(book.get());
        if(copies.isEmpty()){
            return new ServiceResponse<>(Optional.empty(), "Book Copies Not Found");
        }

        BookCopy rentedCopy = null;
        for(BookCopy copy : copies)
            if(copy.getRentalStatus().equals(RentalStatus.FREE)){
                rentedCopy = copy;
                break;
            }
        if(rentedCopy==null)
            return new ServiceResponse<>(Optional.empty(), "No free copies found!");

        rentedCopy.setRentalStatus(RentalStatus.RENTED);
        bookCopyRepository.save(rentedCopy);
        Rental rental = new Rental(reader.get(), rentedCopy, rentalForm.getRentalInWeeks());
        rental.setStatus(RentalStatus.ACTIVE);
        rentalRepository.save(rental);

        return new ServiceResponse<>(Optional.of(rental), "rental ");
    }

    public ServiceResponse<Rental> returnBook(Rental rental){
        if(!rentalRepository.existsById(rental.getRentalId())){
            return new ServiceResponse<>(Optional.empty(), "Rental Not Found");
        }
        Rental rentalDb = rentalRepository.getById(rental.getRentalId());
        rentalDb.setRentalReturnDate(new Date(System.currentTimeMillis()));
        LocalDate start = rentalDb.getRentalDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = rentalDb.getRentalReturnDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int DayDiff = (int) ChronoUnit.DAYS.between(end, start);
        int DiffInWeeks = Math.floorDiv(DayDiff,7);
        float penalty = (rentalDb.getTimeOfRentalInWeeks()-
                DiffInWeeks)*Consts.OVERDUE_PENALTY_PER_WEEK;
        if(DiffInWeeks>rentalDb.getTimeOfRentalInWeeks()){
            rentalDb.setPenalty(penalty);
            rentalDb.setStatus(RentalStatus.OVERDUE);
        }
        else {
            rentalDb.setPenalty(0f);
            rental.setStatus(RentalStatus.INACTIVE);

        }
        Optional<BookCopy> copyDb = bookCopyRepository.findById(rentalDb.getBookCopy().getCopyId());
        if(copyDb.isEmpty()){
            return new ServiceResponse<>(Optional.empty(), "Book Copy Not Found");
        }
        BookCopy copy = copyDb.get();
        copy.setRentalStatus(RentalStatus.FREE);
        bookCopyRepository.save(copy);
        rentalRepository.save(rentalDb);

        if(penalty>0){
            Reader reader = rentalDb.getReader();
            reader.setPenalty(reader.getPenalty()+penalty);
            readerRepository.save(reader);
        }
        return new ServiceResponse<>(Optional.of(rentalDb), "Rental ");
    }

    public ServiceResponse<List<Rental>> getAllRentalsForReader(Reader reader){
        Optional<List<Rental>> list = rentalRepository.findAllByReader(reader);
        if(list.isEmpty()) return new ServiceResponse<>(Optional.empty(), "No Rental Found");
        return new ServiceResponse<>(list, "Rentals found");
    }

    public ServiceResponse<List<Rental>> getAllActiveRentalsForReader(Reader reader){
        Optional<List<Rental>> list = rentalRepository.findAllByReader(reader);
        if(list.isEmpty()) return new ServiceResponse<>(Optional.empty(), "No active Rental Found");
        List<Rental> mathcingList = list.get().stream().filter(rental -> rental.getStatus().equals(RentalStatus.ACTIVE)).toList();
        return new ServiceResponse<>(Optional.of(mathcingList), "Active rentals found");
    }

    public ServiceResponse<Rental> prolongateRental(ProlongateForm prolongateForm){

        int prolongationInWeeks = prolongateForm.getProlongationInWeeks();
        Rental rental = rentalRepository.findById(prolongateForm.getRentalId()).orElse(null);
        if(rental == null){
            return new ServiceResponse<>(Optional.empty(),"Rental not found");
        }
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

        if (!bookFilter.getFilter().isEmpty()) {
            filteredList = bookRepository.findByTitleStartingWithIgnoreCase(bookFilter.getFilter());
        } else {
            List<Book> allBooks = bookRepository.findAll();
            filteredList = applyFilters(allBooks, bookFilter);
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

        if (bookFilter.getAuthor() != null) {
            filteredList = filteredList.stream()
                    .filter(book -> book.getBookAuthor().equals(bookFilter.getAuthor()))
                    .toList();
        }
        if (bookFilter.getBookCategory() != null) {
            filteredList = filteredList.stream()
                    .filter(book -> book.getBookCategory().toString().equals(bookFilter.getBookCategory()))
                    .toList();
        }
        if (bookFilter.getBookType() != null) {
            filteredList = filteredList.stream()
                    .filter(book -> book.getBookType().toString().equals(bookFilter.getBookType()))
                    .toList();
        }
        if (bookFilter.getMinBookRating() != null) {
            filteredList = filteredList.stream()
                    .filter(book -> book.getAverageBookRating() >= bookFilter.getMinBookRating())
                    .toList();
        }

        return filteredList;
    }

    public ServiceResponse<List<NewsPost>> getNewsPostsBetweenDates(DateRange dateRange){
        List<NewsPost> list;
        if(dateRange!=null)
        list = newsPostRepository.findNewsPostsByCreateTimeBetween(dateRange.getStartDate(),dateRange.getEndDate());
        else list = newsPostRepository.findAll();
        if(list.isEmpty()) return new ServiceResponse<>(Optional.empty(), "No News Posts Found");
        return new ServiceResponse<>(Optional.of(list), "News Posts Found");
    }

    public ServiceResponse<BookRating> addOrUpdateBookRating(BookRatingForm bookRatingForm){
        if(!bookRepository.existsById(bookRatingForm.getBookId())){
            return new ServiceResponse<>(Optional.empty(),"Book not found");
        }
        if(!bookRepository.existsById(bookRatingForm.getReaderId())){
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
 }
