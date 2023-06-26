package ru.practicum.booking;

import lombok.*;

import java.time.LocalDateTime;


public class BookingDtoMini {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer itemId;
    private Integer bookerId;
    private BookingStatus status;
}
