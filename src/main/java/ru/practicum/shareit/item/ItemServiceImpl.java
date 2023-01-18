package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enumBooking.Status;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentIncomingDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto save(Long userId, ItemInDto itemDto) {
        User user = userRepository.get(userId);
        if (Objects.isNull(itemDto.getAvailable())) {
            throw new ConflictException("Available can't be NULL");
        }

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, ItemInDto itemDto) {
        userRepository.get(userId);
        Item item = itemRepository.get(itemId);
        if (!userId.equals(item.getOwner().getId())) {
            throw new ForbiddenException("User with ID=" + userId + " not owner for item with ID=" + itemId);
        }
        item = checkUpdate(itemId, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public void deleteById(Long itemId, Long userId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        Item item = itemRepository.get(itemId);
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            return ItemMapper.toItemDto(
                    item,
                    bookingRepository.getLastForItem(itemId, now, Status.APPROVED),
                    bookingRepository.getNextForItem(itemId, now, Status.APPROVED),
                    commentRepository.findAllByItemIdOrderByCreatedDesc(itemId));
        }
        return ItemMapper.toItemDto(item, commentRepository.findAllByItemIdOrderByCreatedDesc(itemId));
    }


    @Override
    public List<ItemDto> findByString(String s) {
        return itemRepository.findByString(s).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findByUserId(Long id) {
        List<Item> items = itemRepository.findAllByOwnerId(id);
        List<Long> itemIds = items
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Map<Long, List<Comment>> commentsByItems = commentRepository.findAllByItemIdInOrderByCreatedDesc(itemIds)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        Map<Long, List<Booking>> bookingsByItems = bookingRepository.getAllByItemIdInAndStatus(itemIds, Status.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();
        return items
                .stream()
                .map(item -> ItemMapper.toItemDto(
                        item,
                        findLastBooking(bookingsByItems.getOrDefault(item.getId(), List.of()), now),
                        findNextBooking(bookingsByItems.getOrDefault(item.getId(), List.of()), now),
                        commentsByItems.getOrDefault(item.getId(), List.of())))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long itemId, CommentIncomingDto commentIncomingDto) {
        User author = userRepository.get(userId);
        Item item = itemRepository.get(itemId);
        if (!isAuthorUsedItem(userId, itemId)) {
            throw new BadRequestException("Comments from users who have not rented a thing are prohibited");
        }
        Comment newComment = CommentMapper.toComment(commentIncomingDto, author, item);
        commentRepository.save(newComment);
        return CommentMapper.toCommentDto(newComment);
    }

    private Item checkUpdate(Long itemId, Item item) {
        Item findItem = itemRepository.get(itemId);
        if (item.getName() != null && !item.getName().isBlank()) {
            findItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            findItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            findItem.setAvailable(item.getAvailable());
        }
        return findItem;
    }

    private boolean isAuthorUsedItem(Long userId, Long itemId) {
        Integer count = bookingRepository.countCompletedBooking(userId, itemId, LocalDateTime.now());
        return (count != null) && (count > 0);
    }

    private Booking findLastBooking(List<Booking> bookings, LocalDateTime now) {
        return bookings
                .stream()
                .filter(b -> !b.getStart().isAfter(now))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }

    private Booking findNextBooking(List<Booking> bookings, LocalDateTime now) {
        return bookings
                .stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }
}