package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDtoResponse itemRequestDto) {
        User user = userService.findById(userId);
        ItemRequest request = new ItemRequest();
        request.setDescription(itemRequestDto.getDescription());
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request);
        return ItemRequestMapper.toDto(request);

    }

    @Override
    public List<ItemRequestDto> findAllByOwner(Long userId) {
        userService.findById(userId);
        return requestToDto(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId));
    }

    @Override
    public List<ItemRequestDto> findAll(Long userId, Pageable pageable) {
        userService.findById(userId);
        return requestToDto(itemRequestRepository.findAllByRequesterIdNot(userId, pageable)
                .getContent());
    }

    @Override
    public ItemRequestDto get(Long userId, Long requestId) {
        userService.findById(userId);
        ItemRequest itemRequest = itemRequestRepository.get(requestId);
        return ItemRequestMapper.toDto(itemRequest, itemRepository.findAllByRequestId(requestId));
    }

    private List<ItemRequestDto> requestToDto(List<ItemRequest> requests) {
        List<Long> requestIds = requests
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Map<Long, List<Item>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(Item::getRequestId));
        return requests
                .stream()
                .map(request -> ItemRequestMapper.toDto(request, itemsByRequest.get(request.getId())))
                .collect(Collectors.toList());
    }

}
