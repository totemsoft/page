package com.totemsoft.page.model.entity;

import java.util.Optional;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Entity(name = "series_data_key")
public class Key {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private long id;

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
    private Set<Tag> tags;

    public Optional<Tag> findTag(TagType tagType) {
        return tags.stream().filter(t -> t.getTagType().equals(tagType)).findFirst();
    }

    public boolean anyMatch(Tag tag) {
        return tags.stream().anyMatch(t -> t.equals(tag));
    }

}
