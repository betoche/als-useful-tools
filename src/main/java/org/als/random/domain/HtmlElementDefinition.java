package org.als.random.domain;

import lombok.Builder;
import org.als.random.enums.HtmlElementType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Builder
public class HtmlElementDefinition {
    private HtmlElementType elementType;
    private String fieldName;
    private String fieldId;

    private JSONArray selectValues;

    // TODO: finish implementing the rest of inputs
    public String getHtmlElement() {
        return switch (elementType) {
            case INPUT_TEXT -> createTextInput();
            case INPUT_NUMERIC_TEXT_FIELD -> createNumericTextInput();
            case INPUT_EMAIL_TEXT -> createEmailInput();
            case INPUT_PASSWORD -> createPasswordInput();
            case INPUT_BUTTON -> null;
            case INPUT_CHECKBOX -> null;
            case INPUT_RADIO -> null;
            case SELECT -> null;
        };
    }

    private String createTextInput() {
        String htmlElementStr = """
                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                    <input class="mdl-textfield__input" type="%s" id="%s" name="%s">
                    <label class="mdl-textfield__label" for="%s">%s</label>
                </div>
                """.formatted(elementType.getType(), fieldId, fieldName, fieldId, fieldName);

        return htmlElementStr;
    }

    private String createNumericTextInput() {
        String htmlElementStr = """
                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                    <input class="mdl-textfield__input" type="%s" id="%s" name="%s">
                    <label class="mdl-textfield__label" for="%s">%s</label>
                    <span class="mdl-textfield__error">Input is not a number!</span>
                </div>
                """.formatted(elementType.getType(), fieldId, fieldName, fieldId, fieldName);

        return htmlElementStr;
    }

    private String createEmailInput() {
        String htmlElementStr = """
                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                    <input class="mdl-textfield__input" type="%s" id="%s" name="%s">
                    <label class="mdl-textfield__label" for="%s">%s</label>
                    <span class="mdl-textfield__helper-text">Example: example@domain.com</span>
                </div>
                """.formatted(elementType.getType(), fieldId, fieldName, fieldId, fieldName);

        return htmlElementStr;
    }

    private String createPasswordInput(){
        String htmlElementStr = """
                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                    <input class="mdl-textfield__input" type="%s" id="%s" name="%s">
                    <label class="mdl-textfield__label" for="%s">%s</label>
                </div>
                """.formatted(elementType.getType(), fieldId, fieldName, fieldId, fieldName);

        return htmlElementStr;
    }

    private String createSelect() {
        String optionsElement = "";
        if( selectValues!=null && selectValues.length() > 0 ) {
            List<String> optionStrList = new ArrayList<>();
            selectValues.iterator().forEachRemaining(obj -> {
                JSONObject jsonObj = (JSONObject)obj;
                optionStrList.add("<li class=\"mdl-menu__item\" data-val=\"%s\">%s</li>".formatted(jsonObj.getLong("primaryKey"), jsonObj.getString("name")));
            });

            optionsElement = String.join(System.lineSeparator(), optionStrList);
        }


        String htmlElement = """
                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label getmdl-select">
                    <input type="text" value="" class="mdl-textfield__input" id="%s" readonly>
                    <input type="hidden" value="" name="%s">
                    <i class="mdl-icon-toggle__label material-icons">keyboard_arrow_down</i>
                    <label for="%s" class="mdl-textfield__label">%s</label>
                    <ul for="sample4" class="mdl-menu mdl-menu--bottom-left mdl-js-menu">
                        %s <!-- Here we insert the option list -->
                    </ul>
                </div>
                """.formatted(fieldId, fieldName, fieldId, fieldName, optionsElement);

        return htmlElement;
    }
}
