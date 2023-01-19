package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDtoResponse itemRequestDto);

    List<ItemRequestDto> findAllByOwner(Long userId);

    List<ItemRequestDto> findAll(Long userId, Pageable pageable);

    ItemRequestDto get(Long userId, Long requestId);

}
