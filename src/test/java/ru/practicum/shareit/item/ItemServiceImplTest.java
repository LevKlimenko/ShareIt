package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImplTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemServiceImpl itemService;
    User user;
    Item item;
    ItemInDto itemInDto;
    Comment comment;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user", "user@mail.ru");
        item = new Item(1L, "item", "itemDesc", true, user, null, null, null, null);
        itemInDto = ItemInDto.builder()
                .name("item")
                .description("itemDesc")
                .available(true)
                .requestId(1L)
                .build();
        comment = new Comment(1L, "comment", item, user, LocalDateTime.now());

    }

    @Test
    void createItemOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(new ItemRequest()));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDto1 = itemService.save(user.getId(), itemInDto);
        assertNotNull(itemDto1);
        assertEquals(ItemDto.class, itemDto1.getClass());
        assertEquals(item.getId(), itemDto1.getId());
        assertEquals(item.getName(), itemDto1.getName());
        assertEquals(item.getDescription(), itemDto1.getDescription());
        assertEquals(item.getAvailable(), itemDto1.getAvailable());
    }

    @Test
    void createItemWithBadUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.save(10L, itemInDto));

    }

    @Test
    void createItemWithBadRequestId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.save(user.getId(), itemInDto));
    }

    @Test
    void updateWithOkRequest() {
        ItemInDto newItem = ItemInDto.builder().name("updateName").description("updateDescr").available(false).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        ItemDto updateItem = itemService.update(item.getId(), user.getId(), newItem);
        assertEquals("updateName", updateItem.getName());
        assertEquals("updateDescr", updateItem.getDescription());
        assertEquals(false, updateItem.getAvailable());
    }

    @Test
    void updateWithBadIdUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.update(item.getId(), user.getId(), itemInDto));
    }

    @Test
    void updateWithBadIdItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(item.getId(), user.getId(), itemInDto));
    }

    @Test
    void updateWithNotOwnerId() {
        User newUser = User.builder().id(2L).name("badUser").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(newUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class, () -> itemService.update(item.getId(), newUser.getId(), itemInDto));
    }

    @Test
    void findByIdIsOk() {
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto findItem = itemService.findById(item.getId(), user.getId());

        assertEquals(item.getName(), findItem.getName());
        assertEquals(item.getDescription(), findItem.getDescription());
        assertTrue(findItem.getComments().isEmpty());
        assertNull(findItem.getLastBooking());
        assertNull(findItem.getNextBooking());
    }

    @Test
    void findByIdWhenItemNotFound() {
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findById(item.getId(), user.getId()));
    }

    @Test
    void findByIdWhenUserNotFound() {
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.findById(anyLong(), item.getId()));
    }

    @Test
    void findAllWithOk() {
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(item));

        List<ItemDto> items = itemService.findByUserId(user.getId(), 0, 1);

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void findAllWithEmptyCollection() {
        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of());

        List<ItemDto> actualItems = itemService.findByUserId(user.getId(), 0, 1);

        assertTrue(actualItems.isEmpty());
    }

    @Test
    void findAllWithEmptyUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.findByUserId(anyLong(), 1, 1));
    }

    @Test
    void searchWithEmptyRequest() {
        List<ItemDto> items = itemService.findByString("", 0, 1);

        assertTrue(items.isEmpty());
    }

    @Test
    void searchWithReturnCollection() {
        when(itemRepository.findByString(anyString(), any())).thenReturn(List.of(item));

        List<ItemDto> actualItems = itemService.findByString("item", 0, 2);

        assertFalse(actualItems.isEmpty());
        assertEquals(1, actualItems.size());
        assertEquals(actualItems.get(0), ItemMapper.toItemDto(item));
    }

    @Test
    void commentedWithOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.countCompletedBooking(anyLong(), anyLong(), any())).thenReturn(1);
        when(commentRepository.save(any())).thenReturn(comment);
        CommentDto actualComment = itemService.createComment(user.getId(), item.getId(),
                CommentMapper.toCommentIncomingDto(comment));

        assertNotNull(actualComment);
        assertEquals(CommentDto.class, actualComment.getClass());
        assertEquals(comment.getId(), actualComment.getId());
        assertEquals(comment.getText(), actualComment.getText());
        assertEquals(user.getName(), actualComment.getAuthorName());
    }

    @Test
    void commentedWithoutBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.countCompletedBooking(anyLong(), anyLong(), any())).thenReturn(0);
        assertThrows(BadRequestException.class,
                () -> itemService.createComment(user.getId(), item.getId(), CommentMapper.toCommentIncomingDto(comment)));
    }

    @Test
    void commentedWithNotFoundItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemService.createComment(user.getId(), item.getId(), CommentMapper.toCommentIncomingDto(comment)));
    }

    @Test
    void commentedWithNotFoundUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemService.createComment(user.getId(), item.getId(), CommentMapper.toCommentIncomingDto(comment)));
    }

}
