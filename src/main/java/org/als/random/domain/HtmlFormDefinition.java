package org.als.random.domain;

import java.util.ArrayList;
import java.util.List;

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

        for( HtmlElementDefinition elemDef : htmlElementDefinitionList ){
            htmlElementStrList.add(elemDef.getHtmlElement()); // TODO: implement the rest of elements
        }

        return htmlElementStrList;
    }
}
