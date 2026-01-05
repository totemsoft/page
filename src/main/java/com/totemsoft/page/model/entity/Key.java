package com.totemsoft.page.model.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "series_data_key")
public class Key {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private long id;

    @NotBlank
    @Column(name = "key_name")
    private String name;

    @Column(name = "key_title")
    private String title;

    @ManyToMany
    @JoinTable(name = "key_tag",
        joinColumns = @JoinColumn(name = "key_id", referencedColumnName = "key_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id"))
    private List<Tag> tags;

}
