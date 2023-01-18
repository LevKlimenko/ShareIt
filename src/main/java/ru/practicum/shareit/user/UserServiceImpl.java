package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        return checkUpdate(id, userDto);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public UserDto findById(Long id) {
        return UserMapper.toUserDto(repository.get(id));
    }

    private UserDto checkUpdate(Long id, UserDto user) {
        User findUser = repository.get(id);
        if (user.getName() != null && !user.getName().isBlank()) {
            findUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            findUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(findUser);
    }
}