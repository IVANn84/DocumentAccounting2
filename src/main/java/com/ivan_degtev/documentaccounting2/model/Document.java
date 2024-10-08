package com.ivan_degtev.documentaccounting2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.ivan_degtev.documentaccounting2.model.interfaces.Authorable;
import com.ivan_degtev.documentaccounting2.model.interfaces.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ForeignKey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = { "author", "type", "available_for" })
public class Document implements BaseEntity, Authorable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String title;

    @NotNull
    private Long number;

//    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
    private User author;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
    private TypeDocument type;

    @JsonProperty(value = "public_document", defaultValue = "false")
    private Boolean publicDocument = false;

    @JsonProperty(value = "available_for")
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "document_user_access",
            joinColumns = @JoinColumn(name = "id_document"),
            inverseJoinColumns = @JoinColumn(name = "id_user",
                    foreignKey = @ForeignKey(name = "fk_document_user",
                            foreignKeyDefinition = "FOREIGN KEY (id_user) REFERENCES users(id_user) ON DELETE CASCADE"))
    )
    @JsonIgnore
    private Set<User> availableFor = new HashSet<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDate creationDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDate updateDate;
}
