package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column(name = "is_available")
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @Column(name = "request_id")
    private Long requestId;
    @Transient
    private Booking lastBooking;
    @Transient
    private Booking nextBooking;
    @Transient
    private List<Comment> comments;

    public Item(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public Item(String name, String description, Boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) &&
                Objects.equals(name, item.name) &&
                Objects.equals(description, item.description) &&
                Objects.equals(available, item.available) &&
                Objects.equals(owner, item.owner) &&
                Objects.equals(requestId, item.requestId) &&
                Objects.equals(lastBooking, item.lastBooking) &&
                Objects.equals(nextBooking, item.nextBooking) &&
                Objects.equals(comments, item.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, owner, requestId, lastBooking, nextBooking, comments);
    }
}