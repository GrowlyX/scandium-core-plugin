package com.solexgames.core.util;

import lombok.experimental.UtilityClass;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HCRival
 * @since 11/1/2019
 */

@UtilityClass
public final class DateUtil {

    private final static Pattern TIME_PATTERN = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);

    public static long parseDateDiff(final String time, final boolean future) {
        final Matcher matcher = DateUtil.TIME_PATTERN.matcher(time);

        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;

        while (matcher.find()) {
            if (matcher.group() != null && !matcher.group().isEmpty()) {
                for (int c = 0; c < matcher.groupCount(); ++c) {
                    if (matcher.group(c) != null && !matcher.group(c).isEmpty()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
                if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {
                    years = Integer.parseInt(matcher.group(1));
                }
                if (matcher.group(2) != null && !matcher.group(2).isEmpty()) {
                    months = Integer.parseInt(matcher.group(2));
                }
                if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
                    weeks = Integer.parseInt(matcher.group(3));
                }
                if (matcher.group(4) != null && !matcher.group(4).isEmpty()) {
                    days = Integer.parseInt(matcher.group(4));
                }
                if (matcher.group(5) != null && !matcher.group(5).isEmpty()) {
                    hours = Integer.parseInt(matcher.group(5));
                }
                if (matcher.group(6) != null && !matcher.group(6).isEmpty()) {
                    minutes = Integer.parseInt(matcher.group(6));
                }
                if (matcher.group(7) != null && !matcher.group(7).isEmpty()) {
                    seconds = Integer.parseInt(matcher.group(7));
                    break;
                }
                break;
            }
        }

        if (!found) {
            return -1L;
        }

        final GregorianCalendar gregorianCalendar = new GregorianCalendar();

        if (years > 0) {
            gregorianCalendar.add(Calendar.YEAR, years * (future ? 1 : -1));
        }
        if (months > 0) {
            gregorianCalendar.add(Calendar.MONTH, months * (future ? 1 : -1));
        }
        if (weeks > 0) {
            gregorianCalendar.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
        }
        if (days > 0) {
            gregorianCalendar.add(Calendar.DATE, days * (future ? 1 : -1));
        }
        if (hours > 0) {
            gregorianCalendar.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
        }
        if (minutes > 0) {
            gregorianCalendar.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
        }
        if (seconds > 0) {
            gregorianCalendar.add(Calendar.SECOND, seconds * (future ? 1 : -1));
        }

        final GregorianCalendar max = new GregorianCalendar();
        max.add(Calendar.YEAR, 10);

        return gregorianCalendar.after(max) ? max.getTimeInMillis() : gregorianCalendar.getTimeInMillis();
    }
}
