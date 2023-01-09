package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enumBooking.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStatusAndEndBefore(Long userId, BookingStatus status, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start,
                                                                             LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDesc(Long userid, Status status);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.item = i.id " +
            "where i.owner=?1 " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerOrderByStartDesc(Long userId);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.item = i.id " +
            "where i.owner=?1 " +
            "and b.start<?2 " +
            "and b.end>?3 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start,
                                                                          LocalDateTime end);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.item = i.id " +
            "where i.owner = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.item = i.id " +
            "where i.owner = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.item = i.id " +
            "where i.owner = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerAndStatusOrderByStartDesc(Long userId, Status status);

    @Query(value = "select * " +
            "from bookings b " +
            "left join items i on  b.item_id = i.id " +
            "where i.id = ?1 " +
            "and b.end_date<?2 " +
            "order by b.end_date desc " +
            "limit 1",
            nativeQuery = true)
    Optional<Booking> findLastBookingByItemId(Long itemId, LocalDateTime dateTime);

    @Query(value = "select * " +
            "from bookings b " +
            "left join items i on  b.item_id = i.id " +
            "where i.id = ?1 " +
            "and b.start_date>?2 " +
            "order by b.start_date desc " +
            "limit 1",
            nativeQuery = true)
    Optional<Booking> findNextBookingByItemId(Long itemId, LocalDateTime dateTime);
}
