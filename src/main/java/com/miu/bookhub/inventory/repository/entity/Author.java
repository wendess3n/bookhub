package com.miu.bookhub.inventory.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(name = "personal_name")
    private String personalName;

    @Column(unique = true)
    private String isni;

    @Column(name = "photo_uri")
    private String photoUri;
}
