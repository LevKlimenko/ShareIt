package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enumBooking.Status;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Sort SORT_BY_DESC = Sort.by(Sort.Direction.DESC, "start");

    default Booking get(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Booking with id=" + id + " not found"));
    }

    List<Booking> findAllByBookerId(Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.item.id = ?2 and b.end <= ?3")
    List<Booking> findAllByBookerIdAndItemId(Long userId, Long itemId, LocalDateTime time);

    @Query("select b from Booking b where  b.booker.id=?1 and b.start>?2")
    List<Booking> findAllFutureForBooker(Long userId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.start>?2")
    List<Booking> findAllFutureForOwner(Long userId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusEquals(Long userid, Status status, Pageable pageable);

    @Query("select b from Booking b where b.booker.id=?1 and ?2 between b.start and b.end order by b.start")
    List<Booking> findAllByBookerCurrent(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id=?1")
    List<Booking> findAllByOwner(Long userId, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id=?1 and ?2 between b.start and b.end order by b.start")
    List<Booking> findAllByOwnerCurrent(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id =?1 and b.end< ?2")
    List<Booking> findAllByOwnerAndEndBefore(Long userId, LocalDateTime end, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id=?1 and b.status=?2")
    List<Booking> findAllByOwnerAndStatus(Long userId, Status status, Pageable pageable);

    Booking getFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(long itemId, LocalDateTime now, Status status);

    Booking getFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(long itemId, LocalDateTime now, Status status);

    default Booking getLastForItem(long itemId, LocalDateTime now, Status status) {
        return getFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(itemId, now, status);
    }

    default Booking getNextForItem(long itemId, LocalDateTime now, Status status) {
        return getFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, now, status);
    }

    List<Booking> getAllByItemIdInAndStatus(Collection<Long> itemIds, Status status);

    @Query("select count (b) from Booking b where b.booker.id=?1 and b.item.id=?2 and b.end<?3 " +
            "and b.status= ru.practicum.shareit.booking.enumBooking.Status.APPROVED")
    Integer countCompletedBooking(Long bookerId, Long itemId, LocalDateTime now);
}