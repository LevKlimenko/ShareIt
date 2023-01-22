package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;

@WebMvcTest(controllers = UserController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserControllerTest {
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    UserDto userDto;
    User user;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(1L, "userName", "user@email.ru");
        user = new User(1L, "userName", "user@email.ru");
    }

    @SneakyThrows
    @Test
    void createUser() {
        Mockito.when(userService.save(ArgumentMatchers.any())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(user.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(user.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(user.getEmail())));

        Mockito.verify(userService).save(ArgumentMatchers.any());
    }

    @SneakyThrows
    @Test
    void updateUserIsOk() {
        UserDto updateUser = new UserDto(userDto.getId(), "updateUserDto", userDto.getEmail());
        Mockito.when(userService.update(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(UserMapper.toUser(updateUser));

        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}",1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(updateUser.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(updateUser.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(updateUser.getEmail())));
        Mockito.verify(userService).update(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @SneakyThrows
    @Test
    void updateUserWithBadId() {
        Mockito.when(userService.update(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}",1)
                        .content(mapper.writeValueAsString((userDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(userService).update(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @SneakyThrows
    @Test
    void getUserByCorrectId() {
        Mockito.when(userService.findById(ArgumentMatchers.anyLong())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/users/{userId}", ArgumentMatchers.anyLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()));
        Mockito.verify(userService).findById(ArgumentMatchers.anyLong());
    }

    @SneakyThrows
    @Test
    void getUserByIncorrectId() {
        Mockito.when(userService.findById(ArgumentMatchers.anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.get("/users/{userId}", ArgumentMatchers.anyLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(userService).findById(ArgumentMatchers.anyLong());
    }

    @SneakyThrows
    @Test
    void getAllIsOk() {
        Mockito.when(userService.getAll()).thenReturn(List.of(user));

        mvc.perform(MockMvcRequestBuilders.get("/users")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].email").value(userDto.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value(userDto.getName()));

        Mockito.verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void getAllWithEmptyColection(){
        Mockito.when(userService.getAll()).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").isEmpty());

        Mockito.verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void deleteWithCorrectId(){
       mvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", ArgumentMatchers.anyLong()))
               .andExpect(MockMvcResultMatchers.status().isOk());

       Mockito.verify(userService).deleteById(ArgumentMatchers.anyLong());
    }

    @SneakyThrows
    @Test
    void deleteWithIncorrectId(){
        Mockito.doThrow(NotFoundException.class).when(userService).deleteById(ArgumentMatchers.anyLong());

        mvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", ArgumentMatchers.anyLong()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(userService).deleteById(ArgumentMatchers.anyLong());
    }
}
