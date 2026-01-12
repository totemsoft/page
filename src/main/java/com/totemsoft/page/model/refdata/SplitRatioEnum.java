package com.totemsoft.page.model.refdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
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

    public String getName() {
        return name();
    }

}
