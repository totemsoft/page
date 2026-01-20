package com.totemsoft.page.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "key_tag")
@IdClass(KeyTagId.class)
public class KeyTag {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "key_id")
    private Long keyId;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "tag_id")
    private Long tagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_id", insertable = false, updatable = false)
    private Key key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", insertable = false, updatable = false)
    private Tag tag;

}
