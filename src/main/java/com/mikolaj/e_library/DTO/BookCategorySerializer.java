package com.mikolaj.e_library.DTO;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class BookCategorySerializer extends JsonSerializer<BookCategory> {
    @Override
    public void serialize(BookCategory bookCategory, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(bookCategory.toString());
    }
}
