package com.island.bookingapi.enumtype;

import javax.persistence.AttributeConverter;

public class StringToEnumConverter implements AttributeConverter< ReservationStatus, String> {
    @Override
    public String convertToDatabaseColumn(ReservationStatus attribute) {
        return attribute == null? null: attribute.getValue();
    }

    @Override
    public ReservationStatus convertToEntityAttribute(String dbData) {
        for(ReservationStatus e: ReservationStatus.values()){
            if(e.getValue().equals(dbData)) return e;
        }
        return null;
    }
}
