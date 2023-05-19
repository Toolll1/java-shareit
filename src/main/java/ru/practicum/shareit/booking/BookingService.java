package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;

    public List<BookingDto> findAll() {

        log.info("Received a request to search for all bookings");

        return bookingRepository.findAll().stream().
                map(BookingMapper::objectToDto).
                sorted(Comparator.comparing(BookingDto::getId)).
                collect(Collectors.toList());
    }

    public BookingDto findById(int bookingId, Integer userId) {

        log.info("Searching for a booking with an id " + bookingId);

        Optional<Booking> booking = bookingRepository.findBooking(bookingId, userId);

        if (booking.isEmpty()) {
            throw new ObjectNotFoundException("There is no booking with this id");
        }

        return BookingMapper.objectToDto(booking.get());
    }

    public BookingDto create(BookingDto booking, Integer userId) {

        booking.setStatus(BookingStatus.WAITING);

        Booking oldBooking = bookingMapper.dtoToObject(booking, userId);

        if (!oldBooking.getItem().getAvailable()) {
            throw new BadRequestException("The item is not available for rent");
        }
        if (oldBooking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("You can't book your own items");
        }
        if (oldBooking.getEnd() == null ||
                oldBooking.getStart() == null ||
                oldBooking.getEnd().isBefore(LocalDateTime.now()) ||
                oldBooking.getStart().isBefore(LocalDateTime.now()) ||
                oldBooking.getEnd().isBefore(oldBooking.getStart()) ||
                oldBooking.getEnd().equals(oldBooking.getStart())) {
            throw new BadRequestException("Incorrect start and/or end date of the lease");
        }

        Item item = oldBooking.getItem();

        itemRepository.save(item);

        Booking newBooking = bookingRepository.save(oldBooking);

        log.info("I received a request to create a booking " + newBooking);

        return BookingMapper.objectToDto(newBooking);
    }

    public BookingDto update(Integer userId, Integer bookingId, Boolean available) {

        BookingDto bookingDto = findById(bookingId, userId);

        if (!bookingDto.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("You can't change the booker");
        }

        Booking booking = bookingMapper.dtoToObject(bookingDto);

        if (!booking.getBookingStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException("it's too late to change anything...");
        }
        if (available) {
            booking.setBookingStatus(BookingStatus.APPROVED);
        } else {
            booking.setBookingStatus(BookingStatus.REJECTED);
        }

        log.info("I received a request to update a booking\n" + booking);

        return BookingMapper.objectToDto(bookingRepository.save(booking));
    }

    public void deleteBooking(int bookingId, Integer userId) {

        if (findById(bookingId, userId).getBooker().getId().equals(userId)) {
            bookingRepository.deleteById(bookingId);
        } else {
            throw new ValidateException("Only its booker can delete an booking");
        }

        log.info("I received a request to delete a booking with an id " + bookingId);
    }

    public List<BookingDto> getUsersBooking(Integer userId, String state) {

        List<Booking> bookings;
        LocalDateTime dateTime = LocalDateTime.now();
        Set<BookingStatus> rejected = Set.of(BookingStatus.REJECTED, BookingStatus.CANCELED);

        switch (state) {
            case "ALL" -> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case "CURRENT" -> bookings = bookingRepository.
                    findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, dateTime, dateTime);
            case "PAST" -> bookings = bookingRepository.
                    findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, dateTime);
            case "FUTURE" -> bookings = bookingRepository.
                    findAllByBookerIdAndStartAfterOrderByStartDesc(userId, dateTime);
            case "WAITING" -> bookings = bookingRepository.
                    findAllByBookerIdAndBookingStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED" -> bookings = bookingRepository.
                    findAllByBookerIdAndBookingStatusInOrderByStartDesc(userId, rejected);
            default -> throw new BadRequestException
                    ("Unknown state: " + state);
        }

        log.info("Getting a list of all bookings of the current user with an id {}, state {} \nresult: {}",
                userId, state, bookings);

        if (bookings.isEmpty()) {
            throw new ValidateException("incorrect data in the request");
        }

        return bookings.stream().map(BookingMapper::objectToDto).collect(Collectors.toList());
    }

    public List<BookingDto> getBookingsForUsersItems(Integer userId, String state) {

        List<Booking> bookings;
        LocalDateTime dateTime = LocalDateTime.now();
        Set<BookingStatus> rejected = Set.of(BookingStatus.REJECTED, BookingStatus.CANCELED);

        switch (state) {
            case "ALL" -> bookings = bookingRepository.findAllByOwnerIdOrderByStartDesc(userId);
            case "CURRENT" -> bookings = bookingRepository.
                    findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, dateTime, dateTime);
            case "PAST" -> bookings = bookingRepository.
                    findAllByOwnerIdAndEndBeforeOrderByStartDesc(userId, dateTime);
            case "FUTURE" -> bookings = bookingRepository.
                    findAllByOwnerIdAndStartAfterOrderByStartDesc(userId, dateTime);
            case "WAITING" -> bookings = bookingRepository.
                    findAllByOwnerIdAndBookingStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED" -> bookings = bookingRepository.
                    findAllByOwnerIdAndBookingStatusInOrderByStartDesc(userId, rejected);
            default -> throw new BadRequestException
                    ("Unknown state: " + state);
        }

        log.info("Getting a list of all bookings of the current users items with an id {}, state {} \nresult: {}",
                userId, state, bookings);

        if (bookings.isEmpty()) {
            throw new ValidateException("incorrect data in the request");
        }

        return bookings.stream().map(BookingMapper::objectToDto).collect(Collectors.toList());
    }
}
