package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    default Item get(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Item with ID=" + id + " not found"));
    }

    @Query("SELECT i FROM Item i" +
            " WHERE (" +
            " UPPER(i.name) LIKE UPPER(CONCAT('%',?1,'%'))" +
            " OR UPPER(i.description) LIKE UPPER(CONCAT('%',?1,'%')))" +
            " AND i.available=true")
    List<Item> findByString(String text);

    Optional<Item> findFirstByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i where i.owner.id = ?1 order by i.id asc")
    List<Item> findAllByOwnerId(Long id);
}