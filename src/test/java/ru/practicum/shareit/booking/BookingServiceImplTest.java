package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.enumBooking.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InvalidStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImplTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;
    BookingIncomingDto bookingIncomingDto;
    Booking booking;
    User user;
    Item item;

    @BeforeEach
    void beforeEach() {
        user = User.builder().id(1L).name("user").email("user@email.ru").build();
        item = Item.builder().id(1L).name("item").description("itemDescr").owner(user)
                .available(true).requestId(2L).lastBooking(null).nextBooking(null).comments(null).build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        bookingIncomingDto = BookingIncomingDto.builder().itemId(item.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();
    }

    @Test
    void saveWithOk() {
        item.setOwner(User.builder().id(2L).build());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto actualBooking = bookingService.save(user.getId(), bookingIncomingDto);

        assertEquals(booking.getId(), actualBooking.getId());
        assertNotNull(actualBooking.getStart());
        assertNotNull(actualBooking.getEnd());
        assertEquals(Status.WAITING, actualBooking.getStatus());
        assertEquals(actualBooking.getBooker().getId(), user.getId());
        verify(bookingRepository).save(any());
    }

    @Test
    void saveUserEmpty() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.save(user.getId(), bookingIncomingDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveItemEmpty() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.save(user.getId(), bookingIncomingDto));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveAvailableFalse() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        item.setOwner(User.builder().id(2L).build());
        item.setAvailable(false);
        assertThrows(BadRequestException.class, () -> bookingService.save(user.getId(), bookingIncomingDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.get(anyLong())).thenReturn(booking);

        BookingDto approvedBookingDto = bookingService.approve(1L, booking.getId(), true);

        assertEquals(approvedBookingDto.getStatus(), Status.APPROVED);
    }

    @Test
    void approveNotOwner() {
        assertThrows(NotFoundException.class, () -> bookingService.approve(2L, booking.getId(), true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveAlreadyApproved() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.get(anyLong())).thenReturn(booking);
        booking.setStatus(Status.APPROVED);
        assertThrows(BadRequestException.class, () -> bookingService.approve(1L, booking.getId(), true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void findByIdOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.get(anyLong())).thenReturn(booking);

        BookingDto foundBookingDto = bookingService.findById(1L, booking.getId());

        assertEquals(foundBookingDto.getId(), 1L);
        assertNotNull(foundBookingDto.getStart());
        assertNotNull(foundBookingDto.getEnd());
        assertEquals(foundBookingDto.getStatus(), Status.WAITING);
        assertEquals(foundBookingDto.getBooker().getId(), 1L);
        assertEquals(foundBookingDto.getItem().getId(), item.getId());
    }

    @Test
    void findByUserIdNotFound() {
        assertThrows(NotFoundException.class, () -> bookingService.findById(3L, booking.getId()));
    }

    @Test
    void findByBookingIdNotFound() {
        lenient().when(bookingRepository.get(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.findById(user.getId(), 3L));
    }

    @Test
    void findAllByBookerIsOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndStatusEquals(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByBookerCurrent(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndEndBefore(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllFutureForBooker(anyLong(), any(), any())).thenReturn(bookings);

        assertEquals(bookingService.findAllForBooker(booking.getBooker().getId(), "ALL", 0, 20).size(), 1);
        assertEquals(bookingService.findAllForBooker(booking.getBooker().getId(), "WAITING", 0, 20).size(), 1);
        assertEquals(bookingService.findAllForBooker(booking.getBooker().getId(), "REJECTED", 0, 20).size(), 1);
        assertEquals(bookingService.findAllForBooker(booking.getBooker().getId(), "CURRENT", 0, 20).size(), 1);
        assertEquals(bookingService.findAllForBooker(booking.getBooker().getId(), "PAST", 0, 20).size(), 1);
        assertEquals(bookingService.findAllForBooker(booking.getBooker().getId(), "FUTURE", 0, 20).size(), 1);
    }

    @Test
    void findAllByBookerBadState(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(InvalidStateException.class,()-> bookingService.findAllForBooker(booking.getBooker().getId(), "BadState", 0, 20));
    }

    @Test
    void findAllByOwnerIsOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findFirstByOwnerId(anyLong())).thenReturn(Optional.of(item));
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        when(bookingRepository.findAllByOwner(anyLong(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByOwnerAndStatus(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByOwnerCurrent(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllByOwnerAndEndBefore(anyLong(), any(), any())).thenReturn(bookings);
        when(bookingRepository.findAllFutureForOwner(anyLong(), any(), any())).thenReturn(bookings);

        assertEquals(bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "ALL", 0, 20).size(), 1);
        assertEquals(bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "WAITING", 0, 20).size(), 1);
        assertEquals(bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "REJECTED", 0, 20).size(), 1);
        assertEquals(bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "CURRENT", 0, 20).size(), 1);
        assertEquals(bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "PAST", 0, 20).size(), 1);
        assertEquals(bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "FUTURE", 0, 20).size(), 1);
    }

    @Test
    void findAllByOwnerBadState(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findFirstByOwnerId(anyLong())).thenReturn(Optional.of(item));
        assertThrows(InvalidStateException.class,()-> bookingService.findAllForOwner(booking.getItem().getOwner().getId(), "BadState", 0, 20));
    }
}
