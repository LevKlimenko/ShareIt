package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    default ItemRequest get(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("ItemRequest with ID=" + id + " not found"));
    }

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(long userId, Pageable pageable);

    Page<ItemRequest> findAllByRequesterIdNot(long userId, Pageable pageable);

}
