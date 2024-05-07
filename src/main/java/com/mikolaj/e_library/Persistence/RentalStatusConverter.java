package com.mikolaj.e_library.Persistence;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mikolaj.e_library.DTO.RentalStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Converter
public class RentalStatusConverter implements AttributeConverter<RentalStatus, String> {
    @Override
    public String convertToDatabaseColumn(RentalStatus rentalStatus) {
        return rentalStatus.getName();
    }
    @Override
    public RentalStatus convertToEntityAttribute(String s) {
        return RentalStatus.fromValue(s);
    }
}
