package com.miu.bookhub.account.repository.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "`user`")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email_address")
    private String emailAddress;

    private String credential;

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ElementCollection(targetClass = Role.class)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    Set<Role> roles;

    @Column(name = "address")
    @OneToMany(mappedBy = "user")
    List<Address> addresses;
}
