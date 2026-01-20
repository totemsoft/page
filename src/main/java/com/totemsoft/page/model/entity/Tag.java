package com.totemsoft.page.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tag")
public final class Tag implements Comparable<Tag> {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @ToString.Include
    @NotBlank
    @Column(name = "tag_name")
    private String name;

    @Column(name = "tag_title")
    private String title;

    @NotNull
    @Column(name = "tag_type_id")
    private Integer tagTypeId;

    @Override
    public int compareTo(Tag t) {
        return name.compareToIgnoreCase(t.name);
    }

}
