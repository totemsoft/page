package com.totemsoft.page.model.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "page")
public final class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "page_id")
    private Long id;

    @NotBlank
    @Column(name = "page_name")
    private String name;

    @OneToMany(mappedBy = "pageId")
    private List<Tab> tabs;

}
