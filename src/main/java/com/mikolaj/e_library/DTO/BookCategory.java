package com.mikolaj.e_library.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BookCategory {
    FANTASY("fantasy"), SCIENCE_FICTION("science_fiction"), ROMANCE("romance"), THRILLER("thriller"), DRAMA("drama"),
    HORROR("horror"), CRIME("crime"), EDUCATION("education");

    private final String name;
    BookCategory(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static BookCategory fromValue(String value) {
        if(value== null) return null;
        for (BookCategory bookCategory : BookCategory.values()) {
            if (bookCategory.name.equalsIgnoreCase(value)) {
                return bookCategory;
            }
        }
        throw new IllegalArgumentException("MembershipStatus: " + value);
    }

    @Override
    public String toString() {
        return name;
    }
}
