package com.totemsoft.page.model.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "page_sub_section")
public class SubSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_section_id")
    private Long id;

    @NotBlank
    @Column(name = "sub_section_name")
    private String name;

    @Column(name = "section_id")
    private long sectionId;

    @ManyToOne
    @JoinColumn(name = "row_tag_type_id")
    private TagType rowTagType;

    @ManyToOne
    @JoinColumn(name = "column_tag_type_id")
    private TagType columnTagType;

    @ManyToMany
    @JoinTable(name = "sub_section_key",
        joinColumns = @JoinColumn(name = "sub_section_id", referencedColumnName = "sub_section_id"),
        inverseJoinColumns = @JoinColumn(name = "key_id", referencedColumnName = "key_id"))
    private Set<Key> keys;

}
