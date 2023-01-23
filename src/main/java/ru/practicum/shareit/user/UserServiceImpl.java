package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
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
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public UserDto save(User user) {
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(Long id, User user) {
        return UserMapper.toUserDto(checkUpdate(id, user));
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public User findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User with ID=" + id + " not found"));
    }

    private User checkUpdate(Long id, User user) {
        User findUser = findById(id);
        if (user.getName() != null && !user.getName().isBlank()) {
            findUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            findUser.setEmail(user.getEmail());
        }
        return findUser;
    }
}