package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDtoResponse itemRequestDto);

    List<ItemRequestDto> findAllByOwner(Long userId, int from,int size);

    List<ItemRequestDto> findAll(Long userId, int from,int size);

    ItemRequestDto get(Long userId, Long requestId);

}
