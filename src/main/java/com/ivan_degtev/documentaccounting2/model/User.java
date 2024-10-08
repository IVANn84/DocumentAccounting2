package com.ivan_degtev.documentaccounting2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.documentaccounting2.model.interfaces.BaseEntity;

import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
@ToString(exclude = { "documents", "openDocuments", "openFileEntities" } )
public class User implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_user")
    private Long idUser;

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank(message = "Username is mandatory")
    @Size(max = 20, message = "Username cannot exceed 20 characters")
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    @NotBlank(message = "Email is mandatory")
    @Size(max = 60, message = "Email cannot exceed 60 characters")
    @Email
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is mandatory")
    @Size(max = 120, message = "Password cannot exceed 120 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "name")
    @NotBlank(message = "Name is mandatory")
    @Size(max = 30, message = "Name cannot exceed 30 characters")
    private String name;

    @Column(name = "last_name")
    @Size(max = 30, message = "Last name cannot exceed 30 characters")
    private String lastName;

    @Column(name = "photo")
    @Size(max = 30, message = "Photo cannot exceed 80 characters")
    private String photo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            schema = "public",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_role"))
    @Column(nullable = false)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id_user")
    @JsonIgnore
    private Set<Document> documents = new HashSet<>();

    @ManyToMany(mappedBy = "availableFor", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Document> openDocuments = new HashSet<>();

    @ManyToMany(mappedBy = "availableFor", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Document> openFileEntities = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @JsonIgnore
    private AddressEntity address;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate creationDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDate updateDate;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    public User(String username, String name, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
