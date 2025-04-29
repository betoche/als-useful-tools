package org.als.random.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringHelper.class);

    public static String snakeToCamelCase(String snakeCase) {
        String camelCase = "";
        Pattern pattern = Pattern.compile("_([a-z])");
        Matcher matcher = pattern.matcher(snakeCase);
        int lastPosition = 0;

        while (matcher.find()) {
            camelCase += snakeCase.substring(lastPosition, matcher.start());
            camelCase += matcher.group(1).toUpperCase();
            lastPosition = matcher.end();
        }

        camelCase += snakeCase.substring(lastPosition);

        if (Character.isUpperCase(camelCase.charAt(0))) {
            camelCase = Character.toLowerCase(camelCase.charAt(0)) + camelCase.substring(1);
        }

        return camelCase;
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static Byte[] stringToByteWrapperArray(String str) {
        byte[] byteArray = str.getBytes(StandardCharsets.UTF_8);
        Byte[] wrapperArray = new Byte[byteArray.length];

        for (int i = 0; i < byteArray.length; i++) {
            wrapperArray[i] = byteArray[i];
        }

        return wrapperArray;
    }

    public static Map<String, Object> getRuleKeyValue(String text, boolean firstTime )  {
        Map<String, Object> resultMap = new HashMap<>();

        int indexOfFirstBracket = 0;
        if( firstTime ) {
            indexOfFirstBracket = text.indexOf("[ ") + 2;
        }
        int indexOfStartEquals = text.indexOf(" = ");
        String subTextRest = text.substring(indexOfStartEquals+3);
        int indexOfEnd = subTextRest.indexOf(" = ");
        String subTextMiddle = "";
        try {
            subTextMiddle = text.substring(indexOfStartEquals + 3, indexOfStartEquals + indexOfEnd + 3);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
        int indexOfLastComma = subTextMiddle.lastIndexOf(", ");

        try {
            String key = text.substring(indexOfFirstBracket, indexOfStartEquals);
            String value = subTextMiddle.substring(0, indexOfLastComma);
            resultMap.put(key, value);
        } catch(Exception e){
            e.printStackTrace();
        }

        String subText = text.substring(indexOfStartEquals + 3 + indexOfLastComma + 2);
        if( subText.indexOf(" = ")>0 && subText.indexOf(" = ") < subText.lastIndexOf(" = ")  ) {
            resultMap.putAll(getRuleKeyValue(subText, false));
        }

        return resultMap;
    }

    public static String getDateStringFromSnapshotFile( String snapshotFile ) {
        String snapDatePatternStr = "2025-04-29-08-49-25.snap";
        int length = snapshotFile.length();
        int beginIdx = length - snapDatePatternStr.length();

        return snapshotFile.substring(beginIdx).replace(".snap", "");
    }

    public static List<String> getDataToTableFormat(String tableName, String[] headers, String[][] data) {
        List<String> formattedData = new ArrayList<>();
        // Calculate maximum column widths
        int[] columnWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();
            for (String[] row : data) {
                try {
                    columnWidths[i] = Math.max(columnWidths[i], row[i].length());
                } catch( Exception e ){
                    LOGGER.error("%s, %s".formatted(e.toString(), e.getMessage()), e);
                }
            }
        }

        String lineStr = "";
        for (int i = 0; i < tableName.length() + 2; i++) {
            lineStr += "-";
        }
        formattedData.add("");
        formattedData.add(lineStr);
        formattedData.add(String.format("%s[%s]", tableName, data.length));
        lineStr = "";
        for (int width : columnWidths) {
            for (int i = 0; i < width + 2; i++) {
                lineStr += "-";
            }
        }
        formattedData.add(lineStr);
        // Print headers
        lineStr = "";
        for (int i = 0; i < headers.length; i++) {
            lineStr += String.format("%-" + (columnWidths[i] + 2) + "s", headers[i]);
        }
        formattedData.add(lineStr);

        // Print separator
        lineStr = "";
        for (int width : columnWidths) {
            for (int i = 0; i < width + 2; i++) {
                lineStr += "-";
            }
        }
        formattedData.add(lineStr);

        // Print data rows
        for (String[] row : data) {
            lineStr = "";
            for (int i = 0; i < row.length; i++) {
                lineStr += String.format("%-" + (columnWidths[i] + 2) + "s", row[i]);
            }
            formattedData.add(lineStr);
        }

        return formattedData;
    }

    public static void printDataToTableFormat(String tableName, String[] headers, String[][] data) {
        // Calculate maximum column widths
        int[] columnWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();
            for (String[] row : data) {
                try {
                    columnWidths[i] = Math.max(columnWidths[i], row[i].length());
                } catch( Exception e ){
                    LOGGER.error("%s, %s".formatted(e.toString(), e.getMessage()), e);
                }
            }
        }

        for (int i = 0; i < tableName.length() + 2; i++) {
            System.out.print("-");
        }
        System.out.println();
        System.out.println(tableName);
        for (int width : columnWidths) {
            for (int i = 0; i < width + 2; i++) {
                System.out.print("-");
            }
        }
        System.out.println();
        // Print headers
        for (int i = 0; i < headers.length; i++) {
            System.out.printf("%-" + (columnWidths[i] + 2) + "s", headers[i]);
        }
        System.out.println();

        // Print separator
        for (int width : columnWidths) {
            for (int i = 0; i < width + 2; i++) {
                System.out.print("-");
            }
        }
        System.out.println();

        // Print data rows
        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                System.out.printf("%-" + (columnWidths[i] + 2) + "s", row[i]);
            }
            System.out.println();
        }
    }
}
