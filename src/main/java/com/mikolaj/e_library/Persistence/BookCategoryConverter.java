package com.mikolaj.e_library.Persistence;

import com.mikolaj.e_library.DTO.BookCategory;
import com.mikolaj.e_library.DTO.RentalStatus;
import com.mikolaj.e_library.model.Book;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BookCategoryConverter implements AttributeConverter<BookCategory, String> {
    @Override
    public String convertToDatabaseColumn(BookCategory bookCategory) {
        return bookCategory.getName();
    }
    @Override
    public BookCategory convertToEntityAttribute(String s) {
        return BookCategory.fromValue(s);
    }
}
