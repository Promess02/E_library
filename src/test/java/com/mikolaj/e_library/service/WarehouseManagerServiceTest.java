package com.mikolaj.e_library.service;

import com.mikolaj.e_library.DTO.ServiceResponse;
import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.BookCopy;
import com.mikolaj.e_library.repo.BookCopyRepository;
import com.mikolaj.e_library.repo.BookRepository;
import com.mikolaj.e_library.repo.WarehouseManagerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WarehouseManagerServiceTest {
    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private WarehouseManagerRepository warehouseManagerRepository;

    @InjectMocks
    private WarehouseManagerService warehouseManagerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addBook_Success() {
        Book book = new Book();
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ServiceResponse<Book> response = warehouseManagerService.addBook(book);

        assertTrue(response.getData().isPresent());
        assertEquals("Book added successfully", response.getMessage());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void addBook_Failure() {
        Book book = new Book();
        when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("Error"));

        ServiceResponse<Book> response = warehouseManagerService.addBook(book);

        assertFalse(response.getData().isPresent());
        assertEquals("Error adding book: Error", response.getMessage());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void updateBook_Success() {
        Book book = new Book();
        book.setBookId(1);
        when(bookRepository.existsById(book.getBookId())).thenReturn(true);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ServiceResponse<Book> response = warehouseManagerService.updateBook(book);

        assertTrue(response.getData().isPresent());
        assertEquals("Book updated successfully", response.getMessage());
        verify(bookRepository, times(1)).existsById(book.getBookId());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void updateBook_NotFound() {
        Book book = new Book();
        book.setBookId(1);
        when(bookRepository.existsById(book.getBookId())).thenReturn(false);

        ServiceResponse<Book> response = warehouseManagerService.updateBook(book);

        assertFalse(response.getData().isPresent());
        assertEquals("Book not found", response.getMessage());
        verify(bookRepository, times(1)).existsById(book.getBookId());
        verify(bookRepository, times(0)).save(book);
    }

    @Test
    void deleteBook_Success() {
        Book book = new Book();
        book.setBookId(1);
        when(bookRepository.existsById(book.getBookId())).thenReturn(true);

        ServiceResponse<Book> response = warehouseManagerService.deleteBook(book);

        assertTrue(response.getData().isPresent());
        assertEquals("Book deleted successfully", response.getMessage());
        verify(bookRepository, times(1)).existsById(book.getBookId());
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void deleteBook_NotFound() {
        Book book = new Book();
        book.setBookId(1);
        when(bookRepository.existsById(book.getBookId())).thenReturn(false);

        ServiceResponse<Book> response = warehouseManagerService.deleteBook(book);

        assertFalse(response.getData().isPresent());
        assertEquals("Book not found", response.getMessage());
        verify(bookRepository, times(1)).existsById(book.getBookId());
        verify(bookRepository, times(0)).delete(book);
    }

    @Test
    void addBookCopy_Success() {
        BookCopy bookCopy = new BookCopy();
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(bookCopy);

        ServiceResponse<BookCopy> response = warehouseManagerService.addBookCopy(bookCopy);

        assertTrue(response.getData().isPresent());
        assertEquals("Book copy added successfully", response.getMessage());
        verify(bookCopyRepository, times(1)).save(bookCopy);
    }

    @Test
    void addBookCopy_Failure() {
        BookCopy bookCopy = new BookCopy();
        when(bookCopyRepository.save(any(BookCopy.class))).thenThrow(new RuntimeException("Error"));

        ServiceResponse<BookCopy> response = warehouseManagerService.addBookCopy(bookCopy);

        assertFalse(response.getData().isPresent());
        assertEquals("Error adding book copy: Error", response.getMessage());
        verify(bookCopyRepository, times(1)).save(bookCopy);
    }

    @Test
    void updateBookCopy_Success() {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setCopyId(1);
        when(bookCopyRepository.existsById(bookCopy.getCopyId())).thenReturn(true);
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(bookCopy);

        ServiceResponse<BookCopy> response = warehouseManagerService.updateBookCopy(bookCopy);

        assertTrue(response.getData().isPresent());
        assertEquals("Book copy updated successfully", response.getMessage());
        verify(bookCopyRepository, times(1)).existsById(bookCopy.getCopyId());
        verify(bookCopyRepository, times(1)).save(bookCopy);
    }

    @Test
    void updateBookCopy_NotFound() {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setCopyId(1);
        when(bookCopyRepository.existsById(bookCopy.getCopyId())).thenReturn(false);

        ServiceResponse<BookCopy> response = warehouseManagerService.updateBookCopy(bookCopy);

        assertFalse(response.getData().isPresent());
        assertEquals("Book copy not found", response.getMessage());
        verify(bookCopyRepository, times(1)).existsById(bookCopy.getCopyId());
        verify(bookCopyRepository, times(0)).save(bookCopy);
    }

    @Test
    void deleteBookCopy_Success() {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setCopyId(1);
        when(bookCopyRepository.existsById(bookCopy.getCopyId())).thenReturn(true);

        ServiceResponse<BookCopy> response = warehouseManagerService.deleteBookCopy(bookCopy);

        assertTrue(response.getData().isPresent());
        assertEquals("Book copy deleted successfully", response.getMessage());
        verify(bookCopyRepository, times(1)).existsById(bookCopy.getCopyId());
        verify(bookCopyRepository, times(1)).delete(bookCopy);
    }

    @Test
    void deleteBookCopy_NotFound() {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setCopyId(1);
        when(bookCopyRepository.existsById(bookCopy.getCopyId())).thenReturn(false);

        ServiceResponse<BookCopy> response = warehouseManagerService.deleteBookCopy(bookCopy);

        assertFalse(response.getData().isPresent());
        assertEquals("Book copy not found", response.getMessage());
        verify(bookCopyRepository, times(1)).existsById(bookCopy.getCopyId());
        verify(bookCopyRepository, times(0)).delete(bookCopy);
    }
}