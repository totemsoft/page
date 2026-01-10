package com.totemsoft.page.model.refdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Getter
@RequiredArgsConstructor
@Log4j2
public enum SplitRatioEnum {

    ONE("1", ""), // div#doc3 creates a 100% page width
    HALF("1/2 - 1/2",         "yui-g"),
    THIRD("1/3 - 1/3 - 1/3",  "yui-gb"),
    ONE_THIRD("1/3 - 2/3",    "yui-gd"),
    TWO_THIRD("2/3 - 1/3",    "yui-gc"),
    ONE_QUATER("1/4 - 3/4",   "yui-gf"),
    THREE_QUATER("3/4 - 1/4", "yui-ge"),
    //QUATER("1/4 - 1/4 - 1/4 - 1/4", "yui-g"), // Nested yui-g
    ;

    private final String title;

    private final String cssClass;

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
        return ONE;
    }

}
