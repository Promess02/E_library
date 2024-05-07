package com.mikolaj.e_library.service;

import com.mikolaj.e_library.DTO.RentalStatus;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.BookCopy;
import com.mikolaj.e_library.model.Reader;
import com.mikolaj.e_library.model.Rental;
import com.mikolaj.e_library.repo.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReaderService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final BookRatingRepository bookRatingRepository ;
    private final BookCopyRepository bookCopyRepository;
    private final ReaderRepository readerRepository;

    public ReaderService(BookRepository bookRepository,
                         UserRepository userRepository, RentalRepository rentalRepository,
                         BookRatingRepository bookRatingRepository,
                         BookCopyRepository bookCopyRepository, ReaderRepository readerRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.readerRepository = readerRepository;
    }

    public ServiceResponse<Reader> updateUserInfo(Reader oldReader, Reader newReader){
        if(!readerRepository.existsById(oldReader.getReaderId())){
            return new ServiceResponse<>(Optional.empty(), "Reader Not Found");
        }
        readerRepository.save(newReader);
        return new ServiceResponse<>(Optional.of(
                readerRepository.getById(oldReader.getReaderId())), "Updated Reader");
    }

    public ServiceResponse<Rental> bookRental(Book book, Reader reader){

        List<BookCopy> copies = bookCopyRepository.findBookCopiesByBook(book);
        if(copies.isEmpty()){
            return new ServiceResponse<>(Optional.empty(), "Book Copies Not Found");
        }
        if(!readerRepository.existsById(reader.getReaderId())){
            return new ServiceResponse<>(Optional.empty(), "reader not found");
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
        Rental rental = new Rental();
        return new ServiceResponse<>(Optional.of(rental), "rental ");
    }
}
