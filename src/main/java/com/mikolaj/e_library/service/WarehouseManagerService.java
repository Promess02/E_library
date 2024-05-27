package com.mikolaj.e_library.service;

import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.BookCopy;
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

    public ServiceResponse<BookCopy> addBookCopy(BookCopy bookCopy) {
        try {
            BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);
            return new ServiceResponse<>(Optional.of(savedBookCopy), "Book copy added successfully");
        } catch (Exception e) {
            return new ServiceResponse<>(Optional.empty(), "Error adding book copy: " + e.getMessage());
        }
    }

    public ServiceResponse<BookCopy> updateBookCopy(BookCopy bookCopy) {
        if (bookCopyRepository.existsById(bookCopy.getCopyId())) {
            try {
                BookCopy updatedBookCopy = bookCopyRepository.save(bookCopy);
                return new ServiceResponse<>(Optional.of(updatedBookCopy), "Book copy updated successfully");
            } catch (Exception e) {
                return new ServiceResponse<>(Optional.empty(), "Error updating book copy: " + e.getMessage());
            }
        } else {
            return new ServiceResponse<>(Optional.empty(), "Book copy not found");
        }
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
