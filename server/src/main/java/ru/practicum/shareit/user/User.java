package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column(unique = true)
    private String email;
}