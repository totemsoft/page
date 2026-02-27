package com.totemsoft.page.model.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "oidc_user")
public class User {

    @Id
    @Column(name = "user_email")
    private String email;

    @NotBlank
    @Column(name = "user_name")
    private String name;

    @Column(name = "user_given_name")
    private String givenName;

    @Column(name = "user_family_name")
    private String familyName;

    @Column(name = "user_middle_name")
    private String middleName;

    @Column(name = "user_gender")
    private String gender;

    @Column(name = "user_birthdate")
    private LocalDate birthdate;

    @Column(name = "user_updated_at")
    Instant updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "oidc_user_authority",
        joinColumns = @JoinColumn(name = "user_email"))
    @Column(name = "user_authority")
    @Builder.Default
    private Set<String> authorities = new HashSet<>();

}
