package org.als.random.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FindReplaceResults {
    private Integer from;
    private Integer to;
    private List<String> matchedLines;

    public FindReplaceResults() {
        this.matchedLines = new ArrayList<>();
    }

    public List<String> getFormmatedResults(int indentSpace) {
        String titleIdent = String.format( "%"+indentSpace+"s", " " );
        String detailsIdent = String.format( "%"+(indentSpace+2)+"s", " " );
        List<String> resultStr = new ArrayList<>();

        resultStr.add(String.format("%sfrom: %s, to: %s, lines: {", titleIdent, from+1, to+1 ));
        for ( String matchedLine : matchedLines ){
            resultStr.add(String.format("%s%s", detailsIdent, matchedLine.trim()));
        }
        resultStr.add(String.format("%s}", titleIdent ));

        return resultStr;
    }

    public List<Integer> getMatchedLineIndexList() {
        List<Integer> indexList = new ArrayList<>();

        for( int i = from ; i <= to ; i++ ) {
            indexList.add(i);
        }

        return indexList;
    }
}
