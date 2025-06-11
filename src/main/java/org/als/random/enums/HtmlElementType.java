package org.als.random.enums;

import lombok.Getter;

@Getter
public enum HtmlElementType {
    INPUT_TEXT(1, "input", "text"),
    INPUT_NUMERIC_TEXT_FIELD(2, "input", "text"), // Numeric TextField
    INPUT_EMAIL_TEXT(3, "input", "text"),
    INPUT_PASSWORD(4, "input", "password"),
    INPUT_BUTTON(5, "input", "button"),
    INPUT_CHECKBOX(6, "input", "checkbox"),
    INPUT_RADIO(7, "input", "radio"),
    SELECT(8, "select", null);

    private int id;
    private String tag;
    private String type;

    private HtmlElementType(int id, String tag, String type){
        this.id = id;
        this.tag = tag;
        this.type = type;
    }
}
