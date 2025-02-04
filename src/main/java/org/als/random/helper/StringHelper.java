package org.als.random.helper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {

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
}
