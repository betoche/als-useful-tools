package org.als.random.domain;

import jakarta.persistence.ManyToOne;
import lombok.Builder;
import org.als.random.enums.HtmlElementType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.als.random.domain.HtmlElementDefinition.*;
import static org.als.random.enums.HtmlElementType.*;

@Builder
public class HtmlFormDefinition {
    private List<HtmlElementDefinition> htmlElementDefinitionList;
    private String action;
    private String dictionaryName;

    public String createHtmlForm() {
        String htmlFormStr = """
                <div id ="mycard">
                  <div class="demo-card-wide mdl-card mdl-shadow--2dp" style="overflow: visible;">
                    <div class="mdl-card__title">
                      <h2 class="mdl-card__title-text">Contact details</h2>
                    </div>
                    <div class="mdl-card__supporting-text" style="overflow: visible;">
                      <form action="#">
                        %s
                      </form>
                    </div>
                  </div>
                </div>
                """.formatted(String.join(System.lineSeparator(), getHtmlElementsStr()));

        return htmlFormStr;
    }

    private List<String> getHtmlElementsStr() {
        List<String> htmlElementStrList = new ArrayList<>();

        for( HtmlElementDefinition elemDef : getHtmlElementDefinitionList() ){
            htmlElementStrList.add(elemDef.getHtmlElement());
        }

        return htmlElementStrList;
    }

    public List<HtmlElementDefinition> getHtmlElementDefinitionList() {
        if(Objects.isNull(htmlElementDefinitionList) ) {
            htmlElementDefinitionList = new ArrayList<>();

            try {
                Class entityClass = Class.forName("org.als.random.entity.%s".formatted(dictionaryName));
                Field[] declaredFields = entityClass.getDeclaredFields();

                for( Field field : declaredFields ) {
                    String fieldName = field.getName();

                    HtmlElementDefinitionBuilder builder = HtmlElementDefinition.builder();
                    builder.fieldName(fieldName);
                    builder.elementType(findHtmlElementType(field));
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return htmlElementDefinitionList;
    }

    private HtmlElementType findHtmlElementType( Field field ) {
        String fieldTypeStr = field.getType().getSimpleName();
        String lowerName = field.getName().toLowerCase();
        Annotation[] fieldAnnotationArray = field.getAnnotations();
        boolean isManyToOne = false;

        for( Annotation annotation : fieldAnnotationArray ){
            if(annotation.annotationType() == ManyToOne.class ){
                isManyToOne = true;
            }
        }

        //TODO: Seguir aqui implementando la logica de los html
        

        return switch (fieldTypeStr) {
            case "String" -> {

                if(lowerName.contains("email")) {
                    yield INPUT_EMAIL_TEXT;
                } else if( lowerName.contains("password") ) {
                    yield INPUT_PASSWORD;
                } else {
                    yield INPUT_TEXT;
                }
            }
            case "Long" -> INPUT_NUMERIC_TEXT_FIELD;

            default -> throw new IllegalStateException("Unsupported Class %s for the field %s, please add its support at HtmlFormDefinition.findHtmlElementType method.".formatted(fieldTypeStr, lowerName));
        };
    }
}
