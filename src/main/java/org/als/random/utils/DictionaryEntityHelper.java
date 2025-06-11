package org.als.random.utils;

import org.als.random.domain.HtmlFormDefinition;
import org.als.random.domain.HtmlFormDefinition.HtmlFormDefinitionBuilder;

public class DictionaryEntityHelper {

    public static HtmlFormDefinition createHtmlFormDefinitionFromEntityName(String entityClass) {
        HtmlFormDefinitionBuilder builder = HtmlFormDefinition.builder();
        builder.dictionaryName(entityClass);

        // TODO: Finish method definition,
        //  here I need to use reflection in order to get all Entity attributes and its data type
        //  to set their respective HtmlElementType

        return builder.build();
    }
}
