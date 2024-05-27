package com.mikolaj.e_library.DTO;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class BookTypeSerializer extends JsonSerializer<BookType> {
    @Override
    public void serialize(BookType bookType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(bookType.toString());
    }
}
