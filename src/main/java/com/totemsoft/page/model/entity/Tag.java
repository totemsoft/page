package com.totemsoft.page.model.entity;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
public class Tag implements Comparable<Tag> {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @NotNull
    @NaturalId
    @Column(name = "tag_type_id")
    private Integer tagTypeId;

    // TODO: remove this dependency
    // Generation of HibernateProxy instances at runtime is not allowed when the configured BytecodeProvider is 'none';
    // your model requires a more advanced BytecodeProvider to be enabled.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_type_id", insertable = false, updatable = false)
    private TagType tagType;

    @ToString.Include
    @NotBlank
    @NaturalId(mutable = true)
    @Column(name = "tag_name")
    private String name;

    //@NotBlank
    @Column(name = "tag_title")
    private String title;

    @Transient
    public String getKey() {
        return name.replace('/', '_');
    }

    @Transient
    public String getLabel() {
        return StringUtils.isBlank(title) ? name : title;
    }

    @Override
    public int compareTo(Tag t) {
        return name.compareToIgnoreCase(t.name);
    }

}
