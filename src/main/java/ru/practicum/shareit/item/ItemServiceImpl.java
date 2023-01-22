package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enumBooking.Status;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentIncomingDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

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
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto save(Long userId, ItemInDto itemDto) {
        User user = findUserById(userId);
        if (Objects.isNull(itemDto.getAvailable())) {
            throw new ConflictException("Available can't be NULL");
        }

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        if(itemDto.getRequestId() !=null) {
            itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(()->new NotFoundException("Request with id=" + itemDto.getRequestId() + " not found"));
        }
        Item newItem = itemRepository.save(item);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, ItemInDto itemDto) {
        findUserById(userId);
        Item item = findItemById(itemId);
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
        Item item = findItemById(itemId);
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
    public List<ItemDto> findByString(String s,int from,int size) {
        return itemRepository.findByString(s,PageRequest.of(from/size,size)).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findByUserId(Long id, int from,int size) {
        findUserById(id);
        List<Item> items = itemRepository.findAllByOwnerId(id, PageRequest.of(from/size,size));
        List<Long> itemIds = items
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Map<Long, List<Comment>> commentsByItems = commentRepository.findAllByItemIdInOrderByCreatedDesc(itemIds)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        Map<Long, List<Booking>> bookingsByItems = bookingRepository.getAllByItemIdInAndStatus(itemIds,Status.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();
        return items
                .stream()
                .map(item -> ItemMapper.toItemDto(
                        item,
                        findLastBooking(bookingsByItems.get(item.getId()), now),
                        findNextBooking(bookingsByItems.get(item.getId()), now),
                        commentsByItems.get(item.getId())))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long itemId, CommentIncomingDto commentIncomingDto) {
        User author = findUserById(userId);
        Item item = findItemById(itemId);
        if (!isAuthorUsedItem(userId, itemId)) {
            throw new BadRequestException("Comments from users who have not rented a thing are prohibited");
        }
        //Comment newComment = CommentMapper.toComment(commentIncomingDto, author, item);
        Comment newComment = commentRepository.save(CommentMapper.toComment(commentIncomingDto, author, item));
        return CommentMapper.toCommentDto(newComment);
    }

    private Item checkUpdate(Long itemId, Item item) {
        Item findItem = findItemById(itemId);
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
        if (bookings == null) {
            return null;
        }
        return bookings
                .stream()
                .filter(b -> b.getEnd().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
    }

    private Booking findNextBooking(List<Booking> bookings, LocalDateTime now) {
        if (bookings == null) {
            return null;
        }
        return bookings
                .stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }
    private User findUserById(Long id){
        return userRepository.findById(id).orElseThrow(()->new NotFoundException("User with id=" + id+" not found"));
    }

    private Item findItemById(Long id){
         return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item with ID=" + id + " not found"));
    }
}