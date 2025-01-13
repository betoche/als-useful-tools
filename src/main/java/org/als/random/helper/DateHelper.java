package org.als.random.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    public static SimpleDateFormat getSimpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    }

    public static Date parseDate(String dateStr ) throws ParseException {
        return getSimpleDateFormat().parse(dateStr);
    }

    public static String formatDate( Date date ){
        return getSimpleDateFormat().format(date);
    }

    public static String getTodaysDateStr() {
        return formatDate(Calendar.getInstance().getTime());
    }
}
