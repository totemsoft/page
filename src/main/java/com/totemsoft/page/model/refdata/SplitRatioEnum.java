package com.totemsoft.page.model.refdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Getter
@RequiredArgsConstructor
@Log4j2
public enum SplitRatioEnum {

    NONE(null),               // 0
    ONE("1"),                 // 1 = #doc3
    HALF("1/2 - 1/2"),        // 2 = yui-g
    THIRD("1/3 - 1/3 - 1/3"), // 3 = yui-gb
    ONE_THIRD("1/3 - 2/3"),   // 4 = yui-gd
    TWO_THIRD("2/3 - 1/3"),   // 5 = yui-gc
    //QUATER("1/4 - 1/4 - 1/4 - 1/4"),
    //ONE_QUATER("1/4 - 3/4"),
    //THREE_QUATER("3/4 - 1/4"),
    ;

    private final String title;

    public int getId() {
        return ordinal();
    }

    public String getName() {
        return name();
    }

    public static SplitRatioEnum valueOf(int id) {
        for (SplitRatioEnum item : values()) {
            if (item.getId() == id) {
                return item;
            }
        }
        log.warn("Unhandled id [{}]", id);
        return NONE;
    }

}
