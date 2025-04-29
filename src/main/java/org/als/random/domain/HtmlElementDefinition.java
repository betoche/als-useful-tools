package org.als.random.domain;

import org.als.random.enums.HmlElementType;
import org.json.JSONObject;

public class HtmlElementDefinition {
    private HmlElementType elementType;
    private String fieldName;
    private String fieldId;

    private JSONObject selectValues;

    // TODO: finish implementing the rest of inputs
    public String getHtmlElement() {
        return switch (elementType){
            case INPUT_TEXT -> createInputText();
            case INPUT_NUMBER -> null;
            case INPUT_EMAIL -> null;
            case INPUT_PASSWORD -> null;
            case INPUT_BUTTON -> null;
            case INPUT_CHECKBOX -> null;
            case INPUT_RADIO -> null;
            case SELECT -> null;
        };
    }

    private String createInputText(){
        String htmlElementStr = """
                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                    <input class="mdl-textfield__input" type="%s" id="%s" name=%s>
                    <label class="mdl-textfield__label" for="firstName">First Name</label>
                </div>
                """.formatted(elementType.getType(), fieldName, fieldId);

        return htmlElementStr;
    }
}
