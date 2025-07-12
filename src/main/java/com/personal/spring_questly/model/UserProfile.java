package com.personal.spring_questly.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_profiles", schema = "quest")
public class UserProfile {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "theme")
    @Builder.Default
    private String theme = "system";

    @Column(name = "language")
    @Builder.Default
    private String language = "id";

    @Column(name = "timezone")
    @Builder.Default
    private String timezone = "Asia/Jakarta";

    @OneToOne
    @JoinColumn(name = "profile_picture_id", referencedColumnName = "file_id")
    private File profilePicture;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", theme='" + theme + '\'' +
                ", language='" + language + '\'' +
                ", timezone='" + timezone + '\'' +
                '}';
    }
}
