package com.mikolaj.e_library.service;

import com.mikolaj.e_library.Consts;
import com.mikolaj.e_library.DTO.*;
import com.mikolaj.e_library.model.*;
import com.mikolaj.e_library.repo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class WorkerService {
    private BookCopyRepository bookCopyRepository;
    private NewsPostRepository newsPostRepository;
    private ReaderRepository readerRepository;
    private BookRepository bookRepository;
    private RentalRepository rentalRepository;
    private WorkerRepository workerRepository;

    public WorkerService(BookCopyRepository bookCopyRepository,NewsPostRepository newsPostRepository, RentalRepository rentalRepository, ReaderRepository readerRepository, BookRepository bookRepository, WorkerRepository workerRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.newsPostRepository = newsPostRepository;
        this.readerRepository = readerRepository;
        this.bookRepository = bookRepository;
        this.workerRepository = workerRepository;
        this.rentalRepository = rentalRepository;
    }

    public ServiceResponse<Rental> rentBookForReader(WorkerRentalForm workerRentalForm){
        if(workerRentalForm.getReaderEmail().isEmpty() || workerRentalForm.getBookId()<=0 || workerRentalForm.getWorkerId()<=0 || workerRentalForm.getRentalInWeeks()<=0)
            return new ServiceResponse<>(Optional.empty(), "bad request send");
        Optional<Reader> reader = readerRepository.findByUserEmail(workerRentalForm.getReaderEmail());
        Optional<Worker> worker = workerRepository.findById(workerRentalForm.getWorkerId());
        Optional<Book> book = bookRepository.findById(workerRentalForm.getBookId());

        if(reader.isEmpty()) return new ServiceResponse<>(Optional.empty(), "No reader found");
        if(worker.isEmpty()) return new ServiceResponse<>(Optional.empty(), "No worker found");
        if(book.isEmpty()) return new ServiceResponse<>(Optional.empty(), "No book found");

        List<BookCopy> copies = bookCopyRepository.findBookCopiesByBook(book.get());
        if(copies.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Book Copies Not Found");


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
        Rental rental = new Rental(reader.get(), rentedCopy, workerRentalForm.getRentalInWeeks());
        rental.setStatus(RentalStatus.ACTIVE);
        rentalRepository.save(rental);

        return new ServiceResponse<>(Optional.of(rental), "Rental saved");

    }

    public ServiceResponse<Rental> returnBook(Rental rental){
        if(!rentalRepository.existsById(rental.getRentalId())){
            return new ServiceResponse<>(Optional.empty(), "Rental Not Found");
        }
        Rental rentalDb = rentalRepository.findById(rental.getRentalId()).get();
        LocalDate start = rentalDb.getRentalDate();
        LocalDate end = LocalDate.now();
        rentalDb.setRentalReturnDate(end);
        int DayDiff = (int) ChronoUnit.DAYS.between(start, end);
        int DiffInWeeks = Math.floorDiv(DayDiff,7);
        float penalty = (DiffInWeeks - rentalDb.getTimeOfRentalInWeeks())* Consts.OVERDUE_PENALTY_PER_WEEK;
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
        return new ServiceResponse<>(Optional.of(rentalDb), "Rental saved");
    }

    public ServiceResponse<NewsPost> addNewsPost(AddNewsPostForm addNewsPostForm){
        Optional<Worker> worker = workerRepository.findById(addNewsPostForm.getWorkerId());
        if(worker.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Worker Not Found");
        NewsPost newsPost = new NewsPost(addNewsPostForm.getName(), addNewsPostForm.getContents(),
                addNewsPostForm.getImageUrl(), worker.get());
        NewsPost newsPostDb = newsPostRepository.save(newsPost);
        return new ServiceResponse<>(Optional.of(newsPost), "News Post saved");
    }

    public ServiceResponse<NewsPost> updateNewsPost(AddNewsPostForm addNewsPostForm){
        Optional<NewsPost> newsPost = newsPostRepository.findById(addNewsPostForm.getNewsPostId());
        if(newsPost.isEmpty()) return new ServiceResponse<>(Optional.empty(), "news post not found");
        Optional<Worker> worker = workerRepository.findById(addNewsPostForm.getWorkerId());
        if(worker.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Worker Not Found");
        NewsPost newsPostDb = newsPost.get();
        newsPostDb.setContents(addNewsPostForm.getContents());
        newsPostDb.setName(addNewsPostForm.getName());
        newsPostDb.setImageUrl(addNewsPostForm.getImageUrl());
        newsPostDb.setWorker(worker.get());
        newsPostRepository.save(newsPostDb);
        return new ServiceResponse<>(Optional.of(newsPostDb), "News Post Updated");
    }

    public ServiceResponse<NewsPost> deleteNewsPost(int newsPostId){
        Optional<NewsPost> newsPost = newsPostRepository.findById(newsPostId);
        if(newsPost.isEmpty()) return new ServiceResponse<>(Optional.empty(), "News Post Not Found");
        newsPostRepository.delete(newsPost.get());
        return new ServiceResponse<>(Optional.of(newsPost.get()), "News Post deleted");
    }

}
