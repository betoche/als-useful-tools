package org.als.random.domain;

import org.als.random.helper.StringHelper;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityCodeGenerator {
    private DatabaseTable table;
    private List<EntityAttributeDetails> attributeDetailList;
    private String className;

    public static final String ONE_SPACES_HTML_CODE = "&nbsp;";
    public static final String TWO_SPACES_HTML_CODE = "&ensp;";
    public static final String FOUR_SPACES_HTML_CODE = "&emsp;";

    public EntityCodeGenerator(DatabaseTable table) {
        this.table = table;
    }

    public String getClassName() {
        if( Objects.isNull(className) ) {
            className = StringHelper.snakeToCamelCase(table.getName().toLowerCase());
        }
        return className;
    }

    public List<EntityAttributeDetails> getAttributeDetailList() {
        if(Objects.isNull(attributeDetailList) ) {
            attributeDetailList = new ArrayList<>();

            for( DatabaseTableColumn column : table.getColumnList() ) {
                attributeDetailList.add(new EntityAttributeDetails(column));
            }
        }
        return attributeDetailList;
    }

    public String getEntityCodeHtml() {
        String nl = System.lineSeparator();
        List<String> classLines = new ArrayList<>();

        classLines.add( "import jakarta.persistence.Column;" );
        classLines.add( "import jakarta.persistence.Entity;" );
        classLines.add( "import jakarta.persistence.Table;" );
        classLines.add( "import lombok.Data;" );
        classLines.add( "import java.sql.Timestamp;" );
        classLines.add( "" );
        classLines.add( "@Entity" );
        classLines.add( String.format("@Table(name=\"%s\")", table.getName() ) );
        classLines.add( "@Data" );
        classLines.add( String.format("public class %s {", getClassName()) );
        for( EntityAttributeDetails attribute : getAttributeDetailList() ) {
            for( String line : attribute.getEntityAttributeDeclarationString() ) {
                classLines.add(String.format("%s%s", FOUR_SPACES_HTML_CODE, line));
            }
        }
        classLines.add( "}" );

        return String.join( "<br>", classLines );
    }
}
