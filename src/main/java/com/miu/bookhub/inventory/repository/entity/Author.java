package com.miu.bookhub.inventory.repository.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(name = "personal_name")
    private String personalName;
    private String isni;

    @Column(name = "photo_uri")
    private String photoUri;
}
