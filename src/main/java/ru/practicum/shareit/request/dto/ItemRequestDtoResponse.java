package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ItemRequestDtoResponse {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String description;
    //private LocalDateTime created;
    //private List<ItemDto> items;
}
