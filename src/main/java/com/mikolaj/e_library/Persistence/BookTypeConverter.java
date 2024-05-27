package com.mikolaj.e_library.Persistence;

import com.mikolaj.e_library.DTO.BookCategory;
import com.mikolaj.e_library.DTO.BookType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BookTypeConverter implements AttributeConverter<BookType, String> {
    @Override
    public String convertToDatabaseColumn(BookType bookType) {
        return bookType.getName();
    }
    @Override
    public BookType convertToEntityAttribute(String s) {
        return BookType.fromValue(s);
    }
}
