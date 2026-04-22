package com.example.photoeditor.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateManager {
    private final Calendar calendar;

    public DateManager() {
        calendar = Calendar.getInstance();
    }

    public void previousDay() { calendar.add(Calendar.DAY_OF_MONTH, -1); }
    public void nextDay() { calendar.add(Calendar.DAY_OF_MONTH, 1); }

    // 👇 加上这个方法
    public void resetCalendar() {
        calendar.setTimeInMillis(System.currentTimeMillis());
    }

    public String getDate() {
        return String.format(Locale.getDefault(), "%d-%d-%d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setDate(String s) {
        String[] arr = s.split("-");
        int y = Integer.parseInt(arr[0]);
        int m = Integer.parseInt(arr[1]) - 1; // month 从 0 开始
        int d = Integer.parseInt(arr[2]);
        calendar.set(y, m, d);
    }

    public static String getYearMonthDayString(long ms) {
        return new SimpleDateFormat("yyyy-M-d", Locale.getDefault()).format(new Date(ms));
    }
}