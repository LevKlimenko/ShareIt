package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

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
        try {
            return UserMapper.toUserDto(repository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("User with email: " + user.getEmail() + " is already exist.");
        }
    }

    @Override
    @Transactional
    public UserDto update(Long id, User user) {
        try {
            return UserMapper.toUserDto(checkUpdate(id, user));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("User with email: " + user.getEmail() + " is already exist.");
        }
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