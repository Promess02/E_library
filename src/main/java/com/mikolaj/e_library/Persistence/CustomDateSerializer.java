package com.mikolaj.e_library.Persistence;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateSerializer extends JsonSerializer<Date> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, IOException {
        String formattedDate = dateFormat.format(date);
        try {
            jsonGenerator.writeString(formattedDate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

