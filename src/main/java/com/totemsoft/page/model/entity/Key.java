package com.totemsoft.page.model.entity;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Table(name = "series_data_key")
public final class Key {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private Long id;

    @ToString.Include
    @NotBlank
    @Column(name = "key_name")
    private String name;

    @Column(name = "key_title")
    private String title;

    @Size(min = 2)
    @ManyToMany
    @JoinTable(name = "key_tag",
        joinColumns = @JoinColumn(name = "key_id", referencedColumnName = "key_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id"))
    private List<Tag> tags;

    public Optional<Tag> findTag(Integer tagTypeId) {
        return tags.stream().filter(t -> t.getTagTypeId().equals(tagTypeId)).findFirst();
    }

    public boolean anyMatch(Long tagId) {
        return tags.stream().anyMatch(t -> t.getId().equals(tagId));
    }

}
