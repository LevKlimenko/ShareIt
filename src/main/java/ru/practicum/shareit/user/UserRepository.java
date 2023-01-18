package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exceptions.NotFoundException;

public interface UserRepository extends JpaRepository<User, Long> {
    default User get(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("User with ID=" + id + " not found"));
    }
}