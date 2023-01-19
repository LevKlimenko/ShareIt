package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public final class ItemRequestMapper {

    /*
    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest, List <Item> items){
        return new ItemRequestDtoResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList())
        );
    }*/

    public ItemRequestDto toDto(ItemRequest from) {
        ItemRequestDto mapped = new ItemRequestDto();
        mapped.setId(from.getId());
        mapped.setDescription(from.getDescription());
        mapped.setCreated(from.getCreated());
        return mapped;
    }

    public ItemRequestDto toDto(ItemRequest from, List<Item> items) {
        ItemRequestDto mapped = toDto(from);
        mapped.setItems(
                items != null
                        ? items
                        .stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList())
                        : List.of());
        return mapped;
    }
}
