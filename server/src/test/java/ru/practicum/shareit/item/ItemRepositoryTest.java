package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        User user = userRepository.save(User.builder()
                .name("user")
                .email("user@mail.ru")
                .build());
        itemRepository.save(Item.builder()
                .id(1L)
                .name("item")
                .description("itemDescr")
                .available(true)
                .owner(user)
                .build());
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
    }

    @Test
    void itemSearchByString() {
        List<Item> items = itemRepository.findByString("item", PageRequest.of(0, 1));
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(Optional.of(1L), Optional.of(items.get(0).getId()));
        assertEquals("itemDescr", items.get(0).getDescription());
    }
}