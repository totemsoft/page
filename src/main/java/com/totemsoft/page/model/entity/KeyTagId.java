package com.totemsoft.page.model.entity;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class KeyTagId {

    @Column(name = "key_id")
    private Long keyId;

    @Column(name = "tag_id")
    private Long tagId;

}
