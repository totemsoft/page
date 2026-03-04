package com.totemsoft.page.model.entity;

import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.NaturalId;

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
public class Key {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private Long id;

    @ToString.Include
    @NotBlank
    @NaturalId(mutable = true)
    @Column(name = "key_name")
    private String name;

    @ToString.Include
    @Column(name = "key_title")
    private String title;

    //@Size(min = 2) // we can save empty tags for key (eg work-in-progress)
    @ManyToMany
    @JoinTable(name = "key_tag",
        joinColumns = @JoinColumn(name = "key_id", referencedColumnName = "key_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id"))
    private List<Tag> tags;

    public Optional<Tag> findTag(Integer tagTypeId) {
        return tags.stream().filter(t -> t.getTagTypeId().equals(tagTypeId)).findFirst();
    }

    public Optional<Tag> findTag(String tagTypeName) {
        return tags.stream().filter(t -> t.getTagType().getName().equals(tagTypeName)).findFirst();
    }

    public boolean anyMatch(Long tagId) {
        return tags.stream().anyMatch(t -> t.getId().equals(tagId));
    }

}
