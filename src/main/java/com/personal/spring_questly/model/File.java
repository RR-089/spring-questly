package com.personal.spring_questly.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "files", schema = "quest")
public class File extends TimeStamp {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "module_name", nullable = false)
    private String moduleName;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "size")
    private Double size;

    @Column(name = "index")
    private Integer index;

    @Column(name = "uri", nullable = false)
    private String uri;
}
