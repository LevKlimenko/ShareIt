package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    User user;
    Item item;
    ItemRequest itemRequest;
    ItemRequestDtoResponse itemRequestDtoResponse;

    @BeforeEach
    void beforeEach() {
        user = User.builder().id(1L).name("user").email("user@email.ru").build();
        item = Item.builder().name("item").requestId(1L).build();
        itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now());
        itemRequestDtoResponse = ItemRequestDtoResponse.builder().description("test").build();
    }

    @Test
    void createWithOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestDto actual = itemRequestService.create(user.getId(), itemRequestDtoResponse);
        assertEquals(1L, actual.getId());
        assertEquals("description", actual.getDescription());
        assertNotNull(actual.getCreated());
    }

    @Test
    void createWithNotFoundUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.create(user.getId(), itemRequestDtoResponse));
    }

    @Test
    void findAllByOwnerIsOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong(), any())).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(List.of());
        List<ItemRequestDto> actual = itemRequestService.findAllByOwner(user.getId(), 1, 1);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());
        assertEquals(itemRequest.getId(), actual.get(0).getId());
        assertEquals(itemRequest.getDescription(), actual.get(0).getDescription());
        assertEquals(List.of(), actual.get(0).getItems());
    }

    @Test
    void findAllIsOk() {
        itemRequest.setRequester(new User(2L, "user2", "user2@email.ru"));
        Page<ItemRequest> requests = new PageImpl<>(List.of(itemRequest));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNot(anyLong(), any())).thenReturn(requests);
        List<ItemRequestDto> requestDtos = itemRequestService.findAll(user.getId(), 0, 20);
        assertEquals(1, requestDtos.size());
        assertEquals(1L, requestDtos.get(0).getId());
        assertEquals("description", requestDtos.get(0).getDescription());
        assertNotNull(requestDtos.get(0).getCreated());
        assertEquals(List.of(),requestDtos.get(0).getItems());
    }

    @Test
    void findByIdIsOk(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.get(anyLong())).thenReturn(itemRequest);

        ItemRequestDto savedRequestDto = itemRequestService.get(user.getId(),itemRequest.getId());
        assertEquals(1L, savedRequestDto.getId());
        assertEquals("description",savedRequestDto.getDescription());
        assertNotNull(savedRequestDto.getCreated());
        assertEquals(List.of(), savedRequestDto.getItems());
    }
}
