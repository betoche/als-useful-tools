package org.als.random.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class FindReplaceSearch {
    private String findStr;
    private String replaceStr;
    private List<Integer> lineIndexOf;
    private String lineSeparator;

    public FindReplaceSearch(String findStr, String replaceStr ) {
        this.findStr = trimLines(findStr);
        this.replaceStr = replaceStr;
        this.lineIndexOf = new ArrayList<>();
    }

    private String trimLines( String str ) {
        String nl = getLineSeparator(str);

        List<String> lineList = new ArrayList<>(Arrays.stream(str.split(nl)).toList());
        lineList.replaceAll(String::trim);

        return String.join(nl, lineList);
    }

    public String getLineSeparator( String str ) {
        String windowsLineSeparator = "\r\n";
        String linuxLineSeparator = "\n";
        String oldMacLineSeparator = "\r";

        if( str.contains(windowsLineSeparator) ) {
            this.lineSeparator = windowsLineSeparator;
        } else if( str.contains(linuxLineSeparator) ) {
            this.lineSeparator = linuxLineSeparator;
        } else {
            this.lineSeparator = oldMacLineSeparator;
        }

        return linuxLineSeparator;
    }

    public void addLineIndexOf( Integer idx ) {
        if( Objects.nonNull(idx) )
            this.lineIndexOf.add(idx);
    }

    public String toString() {
        String nl = System.lineSeparator();
        return String.format("%s{ Search: %s%s, Replace: %s%s, just-remove: %s }", nl, nl, findStr, nl, replaceStr);
    }
}
