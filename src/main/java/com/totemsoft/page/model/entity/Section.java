package com.totemsoft.page.model.entity;

import java.util.List;

import com.totemsoft.page.model.refdata.SplitRatioEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Entity(name = "page_section")
public class Section {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private long id;

    @ToString.Include
    @NotBlank
    @Column(name = "section_name")
    private String name;

    /**
     * vertical position (row index within the tab: 0..n) (ORDER BY)
     */
    @Column(name = "section_index")
    private int index;

    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "section_split_ratio")
    private SplitRatioEnum splitRatio = SplitRatioEnum.ONE;

    @Column(name = "tab_id")
    private long tabId;

    @Size(max = 3)
    @OneToMany(mappedBy = "sectionId")
    private List<SubSection> subSections;

}
