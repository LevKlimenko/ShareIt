package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        when(userService.save(any())).thenReturn(userDto);

        mvc.perform(MockMvcRequestBuilders.post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(user.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail())));

        verify(userService).save(any());
    }

    @SneakyThrows
    @Test
    void createUserWithEmptyName() {
        when(userService.save(any())).thenThrow(BadRequestException.class);
        userDto = UserDto.builder()
                .id(1L)
                .name("")
                .email("user@email.ru")
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createUserWithOnlySpaceInName() {
        when(userService.save(any())).thenThrow(BadRequestException.class);
        userDto = UserDto.builder()
                .id(1L)
                .name(" ")
                .email("user@email.ru")
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createUserWithBadEmail() {
        when(userService.save(any())).thenThrow(BadRequestException.class);
        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user.ru")
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createUserWithEmptyEmail() {
        when(userService.save(any())).thenThrow(BadRequestException.class);
        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("")
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createUserWithOnlySpaceInEmail() {
        when(userService.save(any())).thenThrow(BadRequestException.class);
        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email(" ")
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateUserIsOk() {
        UserDto updateUser = new UserDto(userDto.getId(), "updateUserDto", userDto.getEmail());
        when(userService.update(anyLong(), any())).thenReturn(updateUser);

        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", 1)
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(updateUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(updateUser.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(updateUser.getEmail())));
        verify(userService).update(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateUserWithBadId() {
        when(userService.update(anyLong(), any())).thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", 1)
                .content(mapper.writeValueAsString((userDto)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(userService).update(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateUserWithNoName() {
        when(userService.update(anyLong(), any())).thenThrow(BadRequestException.class);
        userDto = UserDto.builder()
                .id(1L)
                .email("user@email.ru")
                .build();
        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", 1)
                .content(mapper.writeValueAsString((userDto)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService).update(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateUserWithOnlySpaceInName() {
        when(userService.update(anyLong(), any())).thenThrow(BadRequestException.class);
        userDto = UserDto.builder()
                .id(1L)
                .name(" ")
                .email("user@email.ru")
                .build();
        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", 1)
                .content(mapper.writeValueAsString((userDto)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService).update(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateUserWithBadEmail() {
        when(userService.update(anyLong(), any())).thenThrow(BadRequestException.class);
        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user.ru")
                .build();
        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", 1)
                .content(mapper.writeValueAsString((userDto)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateUserWithOnlySpaceInEmail() {
        when(userService.update(anyLong(), any())).thenThrow(BadRequestException.class);
        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email(" ")
                .build();
        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", 1)
                .content(mapper.writeValueAsString((userDto)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @SneakyThrows
    @Test
    void getUserByCorrectId() {
        when(userService.findById(anyLong())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/users/{userId}", anyLong())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.name").value(userDto.getName()));
        verify(userService).findById(anyLong());
    }

    @SneakyThrows
    @Test
    void getUserByIncorrectId() {
        when(userService.findById(anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.get("/users/{userId}", anyLong())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(userService).findById(anyLong());
    }

    @SneakyThrows
    @Test
    void getAllIsOk() {
        when(userService.getAll()).thenReturn(List.of(user));
        mvc.perform(MockMvcRequestBuilders.get("/users")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$.[0].email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.[0].name").value(userDto.getName()));

        verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void getAllWithEmptyCollection() {
        when(userService.getAll()).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/users")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());

        verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void deleteWithCorrectId() {
        mvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", anyLong()))
                .andExpect(status().isOk());

        verify(userService).deleteById(anyLong());
    }

    @SneakyThrows
    @Test
    void deleteWithIncorrectId() {
        Mockito.doThrow(NotFoundException.class).when(userService).deleteById(anyLong());

        mvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", anyLong()))
                .andExpect(status().isNotFound());

        verify(userService).deleteById(anyLong());
    }
}