package com.totemsoft.page.model.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class KeyTagId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "key_id")
    private Long keyId;

    @Column(name = "tag_id")
    private Long tagId;

}
