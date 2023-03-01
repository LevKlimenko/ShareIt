package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public final class ItemRequestMapper {


    public ItemRequestDto toDto(ItemRequest from) {
        ItemRequestDto mapped = new ItemRequestDto();
        mapped.setId(from.getId());
        mapped.setDescription(from.getDescription());
        mapped.setCreated(from.getCreated());
        return mapped;
    }

    public ItemRequestDto toDto(ItemRequest from, List<Item> items) {
        ItemRequestDto mapped = toDto(from);
        mapped.setItems(items
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
        return mapped;
    }
}
