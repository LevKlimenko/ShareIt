package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i" +
            " WHERE (" +
            " UPPER(i.name) LIKE UPPER(CONCAT('%',?1,'%'))" +
            " OR UPPER(i.description) LIKE UPPER(CONCAT('%',?1,'%')))" +
            " AND i.available=true")
    List<Item> findByString(String text, Pageable pageable);

    Optional<Item> findFirstByOwnerId(Long ownerId);

    List<Item> findAllByOwnerIdOrderById(Long id, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByRequestIdIn(Collection<Long> requestIds);
}