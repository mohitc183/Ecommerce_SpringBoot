package com.ecommerce.sb_ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Size(max = 20)
    private String userName;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;


    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    //User & Roles Relation
    @Setter
    @Getter
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
                fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
                joinColumns = @JoinColumn(name ="user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    //User & Addresses Relation
    @Getter
    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_address",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "address_id"))
    private List<Address> addresses = new ArrayList<>();

    //User & Products Relation (If User is a Seller)

    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE}, orphanRemoval = true)
    private Cart cart;


    @ToString.Exclude
    @OneToMany(mappedBy = "user",
                cascade = {CascadeType.PERSIST, CascadeType.MERGE},
                orphanRemoval = true)
    private Set<Product> products;

}
