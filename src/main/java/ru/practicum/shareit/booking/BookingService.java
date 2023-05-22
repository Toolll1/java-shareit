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

        return bookingRepository.findAll().stream().map(BookingMapper::objectToDto).sorted(Comparator.comparing(BookingDto::getId)).collect(Collectors.toList());
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

        checkCorrectness(oldBooking, userId);

        Item item = oldBooking.getItem();

        itemRepository.save(item);

        Booking newBooking = bookingRepository.save(oldBooking);

        log.info("I received a request to create a booking " + newBooking);

        return BookingMapper.objectToDto(newBooking);
    }

    private void checkCorrectness(Booking oldBooking, Integer userId) {

        if (!oldBooking.getItem().getAvailable()) {
            throw new BadRequestException("The item is not available for rent");
        }
        if (oldBooking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("You can't book your own items");
        }
        if (oldBooking.getEnd() == null || oldBooking.getStart() == null || oldBooking.getEnd().isBefore(LocalDateTime.now()) || oldBooking.getStart().isBefore(LocalDateTime.now()) || oldBooking.getEnd().isBefore(oldBooking.getStart()) || oldBooking.getEnd().equals(oldBooking.getStart())) {
            throw new BadRequestException("Incorrect start and/or end date of the lease");
        }
    }

    public BookingDto update(Integer userId, Integer bookingId, Boolean available) {

        BookingDto bookingDto = findById(bookingId, userId);

        if (!bookingDto.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("You can't change the booker");
        }

        Booking booking = bookingMapper.dtoToObject(bookingDto);

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException("it's too late to change anything...");
        }
        if (available) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
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
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, dateTime, dateTime);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, dateTime);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, dateTime);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusInOrderByStartDesc(userId, rejected);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }

        log.info("Getting a list of all bookings of the current user with an id {}, state {} \nresult: {}", userId, state, bookings);

        if (bookings.isEmpty()) {
            throw new ValidateException("incorrect data in the request");
        }

        return bookings.stream().map(BookingMapper::objectToDto).collect(Collectors.toList());
    }

    public List<BookingDto> getBookingsForUsersItems(Integer userId, String state) {

        List<Booking> bookings;
        LocalDateTime dateTime = LocalDateTime.now();
        Set<BookingStatus> rejected = Set.of(BookingStatus.REJECTED, BookingStatus.CANCELED);

        if (state.equals("ALL")) {
            bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        } else if (state.equals("CURRENT")) {
            bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, dateTime, dateTime);
        } else if (state.equals("PAST")) {
            bookings = bookingRepository.findAllByOwnerIdAndEndBeforeOrderByStartDesc(userId, dateTime);
        } else if (state.equals("FUTURE")) {
            bookings = bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(userId, dateTime);
        } else if (state.equals("WAITING")) {
            bookings = bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
        } else if (state.equals("REJECTED")) {
            bookings = bookingRepository.findAllByOwnerIdAndStatusInOrderByStartDesc(userId, rejected);
        } else {
            throw new BadRequestException("Unknown state: " + state);
        }

        log.info("Getting a list of all bookings of the current users items with an id {}, state {} \nresult: {}", userId, state, bookings);

        if (bookings.isEmpty()) {
            throw new ValidateException("incorrect data in the request");
        }

        return bookings.stream().map(BookingMapper::objectToDto).collect(Collectors.toList());
    }
}
