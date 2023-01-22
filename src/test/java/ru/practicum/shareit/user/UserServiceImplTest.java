package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;

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
    void beforeEach(){
        user = new User(1L,"user","user@email.ru");
    }

    @Test
    void saveCorrect(){
        Mockito.when(userRepository.save(ArgumentMatchers.any())).thenReturn(user);

        User checkUser = userService.save(user);
        Assertions.assertEquals(user,checkUser);
        Mockito.verify(userRepository).save(ArgumentMatchers.any());
    }

    @Test
    void updateWithCorrectId(){
        User newUser = new User(user.getId(), "updateName","updateEmail@email.ru");
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(user));
        User updateUser = userService.update(newUser.getId(), newUser);
        Assertions.assertEquals(newUser.getName(),updateUser.getName());
        Assertions.assertEquals(newUser.getEmail(),updateUser.getEmail());
    }

    @Test
    void updateWithIncorrectId(){
        User newUser = new User(user.getId(), "updateName","updateEmail@email.ru");
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,() -> userService.update(newUser.getId(),newUser));
    }

    @Test
    void getByCorrectId(){
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(user));
        User checkUser = userService.findById(ArgumentMatchers.anyLong());
        Assertions.assertEquals(user,checkUser);
    }

    @Test
    void getByIncorrectId(){
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, ()-> userService.findById(ArgumentMatchers.anyLong()));
    }

    @Test
    void getAllWithCollectionUser(){
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> users = userService.getAll();
        Assertions.assertEquals(1,users.size());
        Assertions.assertEquals(user,users.get(0));
    }

    @Test
    void getAllWithEmptyCollection(){
        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<User> users = userService.getAll();
        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    void deleteOk(){
        userService.deleteById(ArgumentMatchers.anyLong());
        Mockito.verify(userRepository).deleteById(ArgumentMatchers.anyLong());
    }

}
