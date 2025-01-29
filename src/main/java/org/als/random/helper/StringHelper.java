package org.als.random.helper;

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
}
