package com.mikolaj.e_library.service;

import com.mikolaj.e_library.DTO.CopiesForm;
import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.BookCopy;
import com.mikolaj.e_library.model.WarehouseManager;
import com.mikolaj.e_library.model.Worker;
import com.mikolaj.e_library.repo.BookCopyRepository;
import com.mikolaj.e_library.repo.BookRepository;
import com.mikolaj.e_library.repo.WarehouseManagerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WarehouseManagerService {
    private BookCopyRepository bookCopyRepository;
    private BookRepository bookRepository;
    private WarehouseManagerRepository warehouseManagerRepository;

    public WarehouseManagerService(BookCopyRepository bookCopyRepository, BookRepository bookRepository, WarehouseManagerRepository warehouseManagerRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
        this.warehouseManagerRepository = warehouseManagerRepository;
    }

    public ServiceResponse<Book> addBook(Book book) {
        try {
            Book savedBook = bookRepository.save(book);
            return new ServiceResponse<>(Optional.of(savedBook), "Book added successfully");
        } catch (Exception e) {
            return new ServiceResponse<>(Optional.empty(), "Error adding book: " + e.getMessage());
        }
    }

    public ServiceResponse<Book> updateBook(Book book) {
        if (bookRepository.existsById(book.getBookId())) {
            try {
                Book updatedBook = bookRepository.save(book);
                return new ServiceResponse<>(Optional.of(updatedBook), "Book updated successfully");
            } catch (Exception e) {
                return new ServiceResponse<>(Optional.empty(), "Error updating book: " + e.getMessage());
            }
        } else {
            return new ServiceResponse<>(Optional.empty(), "Book not found");
        }
    }

    public ServiceResponse<Book> deleteBook(Book book) {
        if (bookRepository.existsById(book.getBookId())) {
            try {
                bookRepository.delete(book);
                return new ServiceResponse<>(Optional.of(book), "Book deleted successfully");
            } catch (Exception e) {
                return new ServiceResponse<>(Optional.empty(), "Error deleting book: " + e.getMessage());
            }
        } else {
            return new ServiceResponse<>(Optional.empty(), "Book not found");
        }
    }

    public ServiceResponse<BookCopy> addBookCopy(CopiesForm copiesForm, int numberOfCopies) {
        try {
            ServiceResponse<BookCopy> response = createBookCopy(copiesForm);
            if(response.getData().isEmpty()) return response;
            BookCopy bookCopy = response.getData().get();
            int iterationTimes = 1;
            if(numberOfCopies>0) iterationTimes = numberOfCopies;
            BookCopy newBookCopy;
            for(int i=0; i<iterationTimes;i++){
                newBookCopy = new BookCopy(bookCopy);
                bookCopyRepository.save(newBookCopy);
            }
            return new ServiceResponse<>(Optional.of(bookCopy), "Book copies added successfully");
        } catch (Exception e) {
            return new ServiceResponse<>(Optional.empty(), "Error adding book copy: " + e.getMessage());
        }
    }

    public ServiceResponse<BookCopy> updateBookCopy(CopiesForm copiesForm) {
        if (bookCopyRepository.existsById(copiesForm.getCopyId())){
            try {
                ServiceResponse<BookCopy> response = createBookCopy(copiesForm);
                if(response.getData().isEmpty()) return response;
                BookCopy updatedBookCopy = response.getData().get();
                updatedBookCopy.setCopyId(copiesForm.getCopyId());
                bookCopyRepository.save(updatedBookCopy);
                return new ServiceResponse<>(Optional.of(updatedBookCopy), "Book copy updated successfully");
            } catch (Exception e) {
                return new ServiceResponse<>(Optional.empty(), "Error updating book copy: " + e.getMessage());
            }
        } else {
            return new ServiceResponse<>(Optional.empty(), "Book copy not found");
        }
    }

    private ServiceResponse<BookCopy> createBookCopy(CopiesForm copiesForm){
        BookCopy bookCopy = new BookCopy();
        Optional<Book> book = bookRepository.findById(copiesForm.getBookId());
        if(book.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Book not found");
        bookCopy.setBook(book.get());
        bookCopy.setShelfPlace(copiesForm.getShelfPlace());
        if(copiesForm.getWorkerId()!=0){
            Optional<WarehouseManager> warehouseManager =
                    warehouseManagerRepository.findById(copiesForm.getWorkerId());
            if(warehouseManager.isEmpty()) return new ServiceResponse<>(Optional.empty(), "Worker not found");
            bookCopy.setAddedBy(warehouseManager.get());
        }
        return new ServiceResponse<>(Optional.of(bookCopy), "ok");
    }

    public ServiceResponse<BookCopy> deleteBookCopy(BookCopy bookCopy) {
        if (bookCopyRepository.existsById(bookCopy.getCopyId())) {
            try {
                bookCopyRepository.delete(bookCopy);
                return new ServiceResponse<>(Optional.of(bookCopy), "Book copy deleted successfully");
            } catch (Exception e) {
                return new ServiceResponse<>(Optional.empty(), "Error deleting book copy: " + e.getMessage());
            }
        } else {
            return new ServiceResponse<>(Optional.empty(), "Book copy not found");
        }
    }
}
