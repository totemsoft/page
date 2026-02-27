package com.totemsoft.page.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String email;

    private String name;

    private String givenName;

    private String familyName;

    private String middleName;

    private String gender;

    private LocalDate birthdate;

    private Instant updatedAt;

    @Builder.Default
    private Set<String> authorities = new HashSet<>();

}
