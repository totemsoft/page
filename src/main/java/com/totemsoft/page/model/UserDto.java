package com.totemsoft.page.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.totemsoft.page.config.SecurityConfig;
import com.totemsoft.page.model.ColumnDef.CellEditorEnum;

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

    public static List<ColumnDef> columns() {
        return List.of(
            ColumnDef.builder()
                .key("email")
                .label("EMail")
                .width(100)
                .build(),
            ColumnDef.builder()
                .key("authorities")
                .label("Roles")
                .editor(CellEditorEnum.CHECKBOX.name().toLowerCase())
                .checkboxOptions(List.of(
                    SecurityConfig.ROLE_ADMIN_PAGE,
                    SecurityConfig.ROLE_ADMIN_USER,
                    SecurityConfig.ROLE_SETUP))
                .build()
            );
    }

}
