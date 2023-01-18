package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enumBooking.State;
import ru.practicum.shareit.booking.enumBooking.Status;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.InvalidStateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        User booker = userRepository.get(userId);
        Item item = itemRepository.get(dto.getItemId());
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
        userRepository.get(userId);
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
        User user = userRepository.get(userId);
        Booking booking = bookingRepository.get(bookingId);
        if (!booking.getBooker().equals(user) && !booking.getItem().getOwner().equals(user)) {
            throw new NotFoundException("It's not possible to get booking id=" + bookingId + " for user id=" + userId);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllForBooker(Long userId, State state) {
        userRepository.get(userId);
        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByBookerId(userId, BookingRepository.SORT_BY_DESC);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatusEquals(userId, Status.WAITING, BookingRepository.SORT_BY_DESC);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatusEquals(userId, Status.REJECTED, BookingRepository.SORT_BY_DESC);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerCurrent(userId, now);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndBefore(userId, now, BookingRepository.SORT_BY_DESC);
                break;
            case FUTURE:
                result = bookingRepository.findAllFutureForBooker(userId, now, BookingRepository.SORT_BY_DESC);
                break;
            default:
                throw new InvalidStateException("Unknown state: " + state);
        }
        return Optional.ofNullable(result)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllForOwner(Long userId, State state) {
        userRepository.get(userId);
        if (itemRepository.findFirstByOwnerId(userId).isEmpty()) {
            return Collections.emptyList();
        }
        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByOwner(userId, BookingRepository.SORT_BY_DESC);
                break;
            case WAITING:
                result = bookingRepository.findAllByOwnerAndStatus(userId, Status.WAITING, BookingRepository.SORT_BY_DESC);
                break;
            case REJECTED:
                result = bookingRepository.findAllByOwnerAndStatus(userId, Status.REJECTED, BookingRepository.SORT_BY_DESC);
                break;
            case CURRENT:
                result = bookingRepository.findAllByOwnerCurrent(userId, now);
                break;
            case PAST:
                result = bookingRepository.findAllByOwnerAndEndBefore(userId, now, BookingRepository.SORT_BY_DESC);
                break;
            case FUTURE:
                result = bookingRepository.findAllFutureForOwner(userId, now, BookingRepository.SORT_BY_DESC);
                break;
            default:
                throw new InvalidStateException("Unknown state: " + state);
        }
        return result
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}