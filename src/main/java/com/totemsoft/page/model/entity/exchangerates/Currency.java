package com.totemsoft.page.model.entity.exchangerates;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "currency")
public class Currency {

    /** TagType name, eg for columns */
    public static final String CURRENCY_BASE = "CURRENCY_BASE";

    /** TagType name, eg for rows */
    public static final String CURRENCY_CODE = "CURRENCY_CODE";

    @EqualsAndHashCode.Include
    @ToString.Include
    @Id
    @Column(name = "currency_code")
    private String code;

    @Column(name = "currency_title")
    private String title;

    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    @Column(name = "currency_base")
    private Boolean base;

}
