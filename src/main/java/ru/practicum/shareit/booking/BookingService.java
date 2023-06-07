package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        return bookingRepository.findAll().stream()
                .map(BookingMapper::objectToDto)
                .sorted(Comparator.comparing(BookingDto::getId))
                .collect(Collectors.toList());
    }

    public BookingDto findBookingById(int bookingId, Integer userId) {

        log.info("Searching for a booking with an id " + bookingId);

        Optional<Booking> booking = bookingRepository.findBooking(bookingId, userId);

        if (booking.isEmpty()) {
            throw new ObjectNotFoundException("There is no booking with this id");
        }

        return BookingMapper.objectToDto(booking.get());
    }

    public BookingDto createBooking(BookingDto booking, Integer userId) {

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
        if (oldBooking.getEnd() == null || oldBooking.getStart() == null ||
                oldBooking.getEnd().isBefore(LocalDateTime.now()) ||
                oldBooking.getStart().isBefore(LocalDateTime.now()) ||
                oldBooking.getEnd().isBefore(oldBooking.getStart()) ||
                oldBooking.getEnd().equals(oldBooking.getStart())) {
            throw new BadRequestException("Incorrect start and/or end date of the lease");
        }
    }

    public BookingDto updateBooking(Integer userId, Integer bookingId, Boolean available) {

        BookingDto bookingDto = findBookingById(bookingId, userId);

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

        if (findBookingById(bookingId, userId).getBooker().getId().equals(userId)) {
            bookingRepository.deleteById(bookingId);
        }


        log.info("I received a request to delete a booking with an id " + bookingId);
    }

    public List<BookingDto> getUsersBooking(Integer userId, String state, Integer from, Integer size) {

        if (from < 0 || size <= 0) {
            throw new BadRequestException("the from parameter must be greater than or equal to 0; size is greater than 0");
        }

        List<Booking> bookings;
        Pageable pageable = pageableCreator(from, size, "start");
        LocalDateTime dateTime = LocalDateTime.now();
        Set<BookingStatus> rejected = Set.of(BookingStatus.REJECTED, BookingStatus.CANCELED);

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerId(userId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, dateTime, dateTime, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, dateTime, pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, dateTime, pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusIn(userId, rejected, pageable);
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

    public List<BookingDto> getBookingsForUsersItems(Integer userId, String state, Integer from, Integer size) {

        if (from < 0 || size <= 0) {
            throw new BadRequestException("the from parameter must be greater than or equal to 0; size is greater than 0");
        }

        List<Booking> bookings;
        Pageable pageable = pageableCreator(from, size, "start");
        LocalDateTime dateTime = LocalDateTime.now();
        Set<BookingStatus> rejected = Set.of(BookingStatus.REJECTED, BookingStatus.CANCELED);

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerId(userId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(userId, dateTime, dateTime, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByOwnerIdAndEndBefore(userId, dateTime, pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByOwnerIdAndStartAfter(userId, dateTime, pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByOwnerIdAndStatusIn(userId, rejected, pageable);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }

        log.info("Getting a list of all bookings of the current users items with an id {}, state {} \nresult: {}", userId, state, bookings);

        if (bookings.isEmpty()) {
            throw new ValidateException("incorrect data in the request");
        }

        return bookings.stream().map(BookingMapper::objectToDto).collect(Collectors.toList());
    }

    private PageRequest pageableCreator(Integer from, Integer size, String sort) {

        return PageRequest.of(from / size, size, Sort.by(sort).descending());
    }
}
