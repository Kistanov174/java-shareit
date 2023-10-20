package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
@RequiredArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String text;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;
    @Column
    private LocalDateTime created = LocalDateTime.now();
}