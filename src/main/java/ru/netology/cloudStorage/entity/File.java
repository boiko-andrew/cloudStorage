package ru.netology.cloudStorage.entity;

import lombok.*;

import javax.persistence.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "files", schema = "public")
public class File {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Setter
    @Getter
    @Column(nullable = false)
    private String name;

    @Getter
    @Column(nullable = false)
    private byte[] content;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    public File(String name, byte[] content, User user) {
        this.name = name;
        this.content = content;
        this.user = user;
    }
}