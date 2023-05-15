package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;

@Service
public class BookingMapper {

    public BookingDto objectToDto(Booking booking) {

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItemId())
                .bookerId(booking.getBookerId())
                .bookingStatus(booking.getBookingStatus())
                .build();
    }

    public Booking dtoToObject(BookingDto dto) {

        return Booking.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .itemId(dto.getItemId())
                .bookerId(dto.getBookerId())
                .bookingStatus(dto.getBookingStatus())
                .build();
    }
}
