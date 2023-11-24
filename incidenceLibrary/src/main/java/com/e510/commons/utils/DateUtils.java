package com.e510.commons.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils
{
    private static final int TODAY = 0;
    private static final int YESTERDAY = -1;
    private static final int TOMORROW = 1;

    public static SimpleDateFormat DATE_T_Z = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static SimpleDateFormat DATE_T = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static SimpleDateFormat DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat DATE_TIME_ES = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static SimpleDateFormat DATE = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat DATE_ES = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat HOUR_MINUTE = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat HOUR_MINUTE_SECOND = new SimpleDateFormat("HH:mm:ss");

    public static Date parseDate(String fecha) {
        try {
            return DATE_TIME.parse(fecha);
        } catch (ParseException e) {
            //ConnectorManager.notificarExcepcion(e);
            e.printStackTrace();
        }
        return new Date();
    }

    public static Date parseDate(String fecha, SimpleDateFormat format) {
        SimpleDateFormat df = format;
        try {
            return df.parse(fecha);
        } catch (ParseException e) {
            //ConnectorManager.notificarExcepcion(e);
            e.printStackTrace();
        }
        catch (Exception ex) {
            //ConnectorManager.notificarExcepcion(e);
            ex.printStackTrace();
        }
        return new Date();
    }

    public static long getDifferenceInDays(Calendar startDate, Calendar endDate) {
        // Get the represented date in milliseconds
        long startDateInMilis = startDate.getTimeInMillis();
        long endDateTimeInMillis = endDate.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = startDateInMilis - endDateTimeInMillis;

        // Calculate difference in days
        long differenceInDays = diff / (24 * 60 * 60 * 1000);

        return differenceInDays;
    }

    private static long getDays(long date) {
        Calendar currentDate = DateUtils.getCurrentDate();
        Calendar startChillDate = DateUtils.getCalendarDate(date);
        return DateUtils.getDifferenceInDays(startChillDate, currentDate);
    }

    public static boolean isToday(long date) {
        return getDays(date) == TODAY;
    }

    public static boolean isYesterday(long date) {
        return getDays(date) == YESTERDAY;
    }

    public static boolean isTomorrow(long date) {
        return getDays(date) == TOMORROW;
    }

    public static String getHour(long date) {
        Calendar calendar = DateUtils.getCalendarDate(date);
        String minutes = (calendar.get(Calendar.MINUTE) < 10) ? "0" + calendar.get(Calendar.MINUTE) : String.valueOf(calendar.get(Calendar.MINUTE));
        String hour = (calendar.get(Calendar.HOUR_OF_DAY) < 10) ? "0" + calendar.get(Calendar.HOUR_OF_DAY) : String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));

        return  hour + ":" + minutes;
    }

    public static String timestampToDateString(long time)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return android.text.format.DateFormat.format("dd/MM/yyyy", cal).toString();
    }

    public static String getDayNameOfWeek(Date date)
    {
        DateFormat format = new SimpleDateFormat("EEEE");
        return format.format(date);
    }
    public static String dateToString(Date date, SimpleDateFormat df) {
        return dateToString(date, df, true);
    }

    public static String dateToString(Date date, SimpleDateFormat df, boolean toCurrentTimeZone) {
        if (toCurrentTimeZone)
        {
            SimpleDateFormat sdf = new SimpleDateFormat(df.toPattern());
            TimeZone tz = TimeZone.getDefault();
            sdf.setTimeZone(tz);
            return sdf.format(date);
        }
        else
        {
            SimpleDateFormat sdf = new SimpleDateFormat(df.toPattern());
            TimeZone tz = TimeZone.getTimeZone("UTC");
            sdf.setTimeZone(tz);
            return sdf.format(date);
        }
    }

    public static String timeToHoursMinutes(String time) {
        try {
            Date date = TIME.parse(time);
            return HOUR_MINUTE.format(date);

        } catch (ParseException e) {
            //ConnectorManager.notificarExcepcion(e);
            e.printStackTrace();
        }
        catch (Exception ex) {
            //ConnectorManager.notificarExcepcion(e);
            ex.printStackTrace();
        }
        return time;
    }

    public static String timeBetweenDates(Date d1, Date d2)
    {
        String res = "";

        long diff = d2.getTime() - d1.getTime();

        //long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        //long diffHours = diff / (60 * 60 * 1000) % 24;
        //long diffDays = diff / (24 * 60 * 60 * 1000);

        res = diffMinutes + "";

        return res;
    }

    public static Calendar getCurrentDate() {
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);
        return now;
    }

    public static Calendar getCalendarDate(long date) {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTimeInMillis(date);

        return calendarDate;
    }

    public static Calendar getCalendarDate(Date date) {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);

        return calendarDate;
    }

    public static int getDifferenceInSeconds(Calendar startDate, Calendar endDate) {
        long startDateInMilis = startDate.getTimeInMillis();
        long endDateTimeInMillis = endDate.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = startDateInMilis - endDateTimeInMillis;

        // Calculate difference in seconds
        long differenceInDays = diff / 1000;

        return (int)differenceInDays;
    }

    public static long getDifferenceInHours(Calendar startDate, Calendar endDate) {
        long startDateInMilis = startDate.getTimeInMillis();
        long endDateTimeInMillis = endDate.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = startDateInMilis - endDateTimeInMillis;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return hours;
    }

    public static long getDifferenceInMinutes(Calendar startDate, Calendar endDate) {
        long startDateInMilis = startDate.getTimeInMillis();
        long endDateTimeInMillis = endDate.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = startDateInMilis - endDateTimeInMillis;
        long seconds = diff / 1000;
        long minutes = seconds / 60;

        return minutes;
    }

    public static String getTimeElapsed(Date date) {
        /*
        String REPLACE_CHAR = "%1";
        int ONE_MINUTE = 60;
        int TWO_MINUTES = 120;
        int ONE_HOUR = 60;
        int TWO_HOURS = 120;
        int ONE_DAY = 24 * 60;
        int TWO_DAYS = 24 * 60 * 2;
        int A_WEEK = 24 * 60 * 7;

        String dateString;
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTimeInMillis(date.getTime());
        Calendar currentDate = getCurrentDate();

        int deltaSeconds = getDifferenceInSeconds(currentDate, calendarDate);
        int deltaMinutes = deltaSeconds / 60;
        int minutes;

        if (deltaSeconds < 5){
            dateString = context.getString(R.string.now);
        } else if (deltaSeconds < ONE_MINUTE) {
            dateString = context.getString(R.string.date_seconds_ago).replace(REPLACE_CHAR, String.valueOf(deltaSeconds));
        } else if (deltaSeconds < TWO_MINUTES) {
            dateString = context.getString(R.string.one_minute_ago);
        } else if (deltaMinutes < ONE_HOUR) {
            dateString = context.getString(R.string.date_minutes_ago).replace(REPLACE_CHAR, String.valueOf(deltaMinutes));
        } else if (deltaMinutes < TWO_HOURS) {
            dateString = context.getString(R.string.one_hour_ago);
        } else if (deltaMinutes < ONE_DAY) {
            minutes = deltaMinutes / 60;
            dateString = context.getString(R.string.date_hours_ago).replace(REPLACE_CHAR, String.valueOf(minutes));
        } else if (deltaMinutes < TWO_DAYS) {
            dateString = context.getString(R.string.yesterday);
        } else if (deltaMinutes < A_WEEK) {
            minutes = deltaMinutes / (60 * 24);
            dateString = context.getString(R.string.date_days_ago).replace(REPLACE_CHAR, String.valueOf(minutes));
        } else {
            dateString = DATE_ES.format(date).toLowerCase();
        }
        */

        //String dateString = ""+android.text.format.DateUtils.getRelativeDateTimeString(context, date.getTime(), android.text.format.DateUtils.MINUTE_IN_MILLIS, android.text.format.DateUtils.WEEK_IN_MILLIS, 0);
        String dateString = ""+android.text.format.DateUtils.getRelativeTimeSpanString(date.getTime(), new Date().getTime(), android.text.format.DateUtils.MINUTE_IN_MILLIS);

        return dateString;
    }

    public static Date getTomorrow()
    {
        return getTomorrow(false);
    }
    public static Date getTomorrow(boolean zeroZero)
    {
        return getNextDayDate(new Date(), zeroZero);
    }

    public static Date getNextDayDate(Date date, boolean zeroZero)
    {
        Calendar calendar = Calendar.getInstance();

        // get a date to represent "today"
        //Date today = calendar.getTime();

        // add one day to the date/calendar
        calendar.setTime(date);

        calendar.add(Calendar.DAY_OF_YEAR, 1);

        if (zeroZero)
        {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }

        // now get "tomorrow"
        Date tomorrow = calendar.getTime();

        return tomorrow;
    }

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String minutesString = "";
        String secondsString = "";

        //Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            if (hours < 10) {
                finalTimerString = "0" + hours + ":";
            } else {
                finalTimerString = hours + ":";
            }
        }

        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }

        // Pre appending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}
