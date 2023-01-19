package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    default ItemRequest get(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("ItemRequest with ID=" + id + " not found"));
    }

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(long userId);

    @Query("select r from ItemRequest r where r.requestor.id <> ?1")
    List<ItemRequest> findAllByOtherUser(long userId, Pageable pageable);
}
