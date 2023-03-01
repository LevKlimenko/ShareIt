package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> save(@Validated(Create.class) @RequestBody UserDto userDto) {
        return userClient.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable("userId") Long id, @Validated(Update.class)
    @RequestBody UserDto userDto) {
        return userClient.updateUser(id, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable("userId") Long id) {
        return userClient.getById(id);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteById(@PathVariable("userId") Long id) {
        return userClient.deleteById(id);
    }
}