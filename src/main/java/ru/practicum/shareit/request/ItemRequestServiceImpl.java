package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    public ItemRequestDtoResponse addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.get(userId);
        return ItemRequestMapper.toItemRequestDtoResponse(
                itemRequestRepository.save(new ItemRequest(0L, itemRequestDto.getDescription(),
                        user, LocalDateTime.now())), new ArrayList<>());
    }

    @Override
    public List<ItemRequestDtoResponse> findAllByOwner(Long userId) {
        userRepository.get(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return requestToDto(requests);
    }

    @Override
    public List<ItemRequestDtoResponse> findAll(Long userId, Pageable pageable) {
    userRepository.get(userId);
    List<ItemRequest> requests = itemRequestRepository.findAllByOtherUser(userId,pageable);
    return requestToDto(requests);
    }

    @Override
    public ItemRequestDtoResponse findRequest(Long userId, Long requestId) {
        userRepository.get(userId);
        ItemRequest itemRequest = itemRequestRepository.get(requestId);
        return ItemRequestMapper.toItemRequestDtoResponse(itemRequest,
                itemRepository.findAllByRequestId(itemRequest.getId()));
    }

    private List<ItemRequestDtoResponse> requestToDto(List<ItemRequest> requests) {
        return requests.stream().map(request -> ItemRequestMapper.toItemRequestDtoResponse(request,
                itemRepository.findAllByRequestId(request.getId()))).collect(Collectors.toList());
    }

}
