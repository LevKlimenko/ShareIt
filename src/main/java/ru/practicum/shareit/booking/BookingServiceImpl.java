package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enumBooking.Status;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.InvalidStateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto save(Long userId, BookingIncomingDto dto) {
        User booker = findUserById(userId);
        Item item = findItemById(dto.getItemId());

        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("The owner of the Item cannot booking his Item");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Item ID=" + item.getId() + "not available now for booking");
        }
        Booking newBooking = BookingMapper.toBooking(dto, item, booker);
        Booking createBooking = bookingRepository.save(newBooking);
        return BookingMapper.toBookingDto(createBooking);
    }

    @Override
    @Transactional
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        findUserById(userId);
        Booking booking = bookingRepository.get(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Item ID=" + booking.getItem().getId() + "doesn't belong to the user ID=" + userId);
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new BadRequestException("Booking id=" + bookingId + " is already approved");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        User user = findUserById(userId);
        Booking booking = bookingRepository.get(bookingId);
        if (!booking.getBooker().equals(user) && !booking.getItem().getOwner().equals(user)) {
            throw new NotFoundException("It's not possible to get booking id=" + bookingId + " for user id=" + userId);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllForBooker(Long userId, String state, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, BookingRepository.SORT_BY_DESC);
        findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result;
        switch (state) {
            case "ALL":
                result = bookingRepository.findAllByBookerId(userId, pageable);
                break;
            case "WAITING":
                result = bookingRepository.findAllByBookerIdAndStatusEquals(userId, Status.WAITING, pageable);
                break;
            case "REJECTED":
                result = bookingRepository.findAllByBookerIdAndStatusEquals(userId, Status.REJECTED, pageable);
                break;
            case "CURRENT":
                result = bookingRepository.findAllByBookerCurrent(userId, now, pageable);
                break;
            case "PAST":
                result = bookingRepository.findAllByBookerIdAndEndBefore(userId, now, pageable);
                break;
            case "FUTURE":
                result = bookingRepository.findAllFutureForBooker(userId, now, pageable);
                break;
            default:
                throw new InvalidStateException("Unknown state: " + state);
        }
        return result
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllForOwner(Long userId, String state, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, BookingRepository.SORT_BY_DESC);
        findUserById(userId);
        if (itemRepository.findFirstByOwnerId(userId).isEmpty()) {
            return Collections.emptyList();
        }
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result;
        switch (state) {
            case "ALL":
                result = bookingRepository.findAllByOwner(userId, pageable);
                break;
            case "WAITING":
                result = bookingRepository.findAllByOwnerAndStatus(userId, Status.WAITING, pageable);
                break;
            case "REJECTED":
                result = bookingRepository.findAllByOwnerAndStatus(userId, Status.REJECTED, pageable);
                break;
            case "CURRENT":
                result = bookingRepository.findAllByOwnerCurrent(userId, now, pageable);
                break;
            case "PAST":
                result = bookingRepository.findAllByOwnerAndEndBefore(userId, now, pageable);
                break;
            case "FUTURE":
                result = bookingRepository.findAllFutureForOwner(userId, now, pageable);
                break;
            default:
                throw new InvalidStateException("Unknown state: " + state);
        }
        return result
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id=" + id + " not found"));
    }

    private Item findItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item with ID=" + id + " not found"));
    }
}