package de.trilobytese.vocab.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String getDate(long timestamp) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return df.format(new Date(timestamp));
    }

    public static String getDateTime(long timestamp) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault());
        return df.format(new Date(timestamp));
    }
}
