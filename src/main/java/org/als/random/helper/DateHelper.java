package org.als.random.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DateHelper {

    public static SimpleDateFormat getSimpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    }

    public static SimpleDateFormat getSimpleDateFormatUS() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    }

    public static Date parseDateUS( String dateStr ) throws ParseException {
        if(Objects.isNull(dateStr) || dateStr.equalsIgnoreCase("null"))
            return null;
        return getSimpleDateFormatUS().parse(dateStr);
    }

    public static SimpleDateFormat getSimpleDateFormatWithTimeZone(){
        return new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    }

    public static Date parseDateWithTimeZone( String dateStr ) throws ParseException {
        if(Objects.isNull(dateStr) || dateStr.equalsIgnoreCase("null"))
            return null;
        return getSimpleDateFormatWithTimeZone().parse(dateStr);
    }

    public static Date parseDate( String dateStr ) throws ParseException {
        return getSimpleDateFormat().parse(dateStr);
    }

    public static String formatDate( Date date ){
        return getSimpleDateFormat().format(date);
    }

    public static String getTodaysDateStr() {
        return formatDate(Calendar.getInstance().getTime());
    }
}
