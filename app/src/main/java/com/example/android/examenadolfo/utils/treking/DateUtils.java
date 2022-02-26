package com.example.android.examenadolfo.utils.treking;

import org.joda.time.DateTime;

public class DateUtils {
    public static String simpleDate() {
        return formattedDate(DateTime.now(), true);
    }
    public static String formattedDate(DateTime dateTime, boolean hours) {
        if (hours) {
            return dateTime.toString("dd/MM/yyyy HH:mm");
        } else {
            return dateTime.toString("dd/MM/yyyy");
        }
    }

}
