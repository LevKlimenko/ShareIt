package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    default ItemRequest get(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("ItemRequest with ID=" + id + " not found"));
    }

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(long userId);

    Page<ItemRequest> findAllByRequesterIdNot(long userId, Pageable pageable);

}
