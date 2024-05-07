package com.mikolaj.e_library.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RentalStatus {
    RENTED("Rented"), FREE("Free"), ACTIVE("active"), INACTIVE("inactive");

    private final String name;
    RentalStatus(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static RentalStatus fromValue(String value) {
        if(value== null) return null;
        for (RentalStatus rentalStatus : RentalStatus.values()) {
            if (rentalStatus.name.equalsIgnoreCase(value)) {
                return rentalStatus;
            }
        }
        throw new IllegalArgumentException("MembershipStatus: " + value);
    }

    @Override
    public String toString() {
        return name;
    }
}
