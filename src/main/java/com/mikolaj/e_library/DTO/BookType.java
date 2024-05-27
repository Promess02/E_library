package com.mikolaj.e_library.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BookType {
    PAPERBACK("paperback"), HARDOVER("hardcover"), EBOOK("ebook"), AUDIOBOOK("audiobook");

    private final String name;
    BookType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static BookType fromValue(String value) {
        if(value== null) return null;
        for (BookType bookType : BookType.values()) {
            if (bookType.name.equalsIgnoreCase(value)) {
                return bookType;
            }
        }
        throw new IllegalArgumentException("MembershipStatus: " + value);
    }

    @Override
    public String toString() {
        return name;
    }
}
