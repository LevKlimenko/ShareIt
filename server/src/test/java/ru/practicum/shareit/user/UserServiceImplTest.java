package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;
    User user;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user", "user@email.ru");
    }

    @Test
    void saveCorrect() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto checkUser = userService.save(user);
        Assertions.assertEquals(UserMapper.toUserDto(user), checkUser);
        verify(userRepository).save(any());
    }

    @Test
    void updateWithCorrectId() {
        User newUser = new User(user.getId(), "updateName", "updateEmail@email.ru");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto updateUser = userService.update(newUser.getId(), newUser);
        assertEquals(newUser.getName(), updateUser.getName());
        assertEquals(newUser.getEmail(), updateUser.getEmail());
    }

    @Test
    void updateWithIncorrectId() {
        User newUser = new User(user.getId(), "updateName", "updateEmail@email.ru");
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.update(newUser.getId(), newUser));
    }

    @Test
    void getByCorrectId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        User checkUser = userService.findById(anyLong());
        assertEquals(user, checkUser);
    }

    @Test
    void getByIncorrectId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findById(anyLong()));
    }

    @Test
    void getAllWithCollectionUser() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> users = userService.getAll();
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test
    void getAllWithEmptyCollection() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<User> users = userService.getAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void deleteOk() {
        userService.deleteById(anyLong());
        verify(userRepository).deleteById(anyLong());
    }
}