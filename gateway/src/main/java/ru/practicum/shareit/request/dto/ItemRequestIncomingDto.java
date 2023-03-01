package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestIncomingDto {
    @NotBlank(groups = {Create.class})
    String description;
}