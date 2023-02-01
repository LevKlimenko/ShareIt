package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDtoResponse itemRequestDto) {
        User user = findUserById(userId);
        ItemRequest request = new ItemRequest();
        request.setDescription(itemRequestDto.getDescription());
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toDto(itemRequestRepository.save(request));

    }

    @Override
    public List<ItemRequestDto> findAllByOwner(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from/size,size);
        findUserById(userId);
        return requestToDto(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId,pageable));
    }

    @Override
    public List<ItemRequestDto> findAll(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from/size,size);
        findUserById(userId);
        return requestToDto(itemRequestRepository.findAllByRequesterIdNot(userId, pageable)
                .getContent());
    }

    @Override
    public ItemRequestDto get(Long userId, Long requestId) {
        findUserById(userId);
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
    private User findUserById(Long id){
        return userRepository.findById(id).orElseThrow(()->new NotFoundException("User with id=" + id+" not found"));
    }


}
