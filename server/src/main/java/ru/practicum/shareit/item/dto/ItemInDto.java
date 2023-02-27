package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemInDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
