package com.sucsoft.wwfb.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FormatDate {
    public FormatDate() {
    }

    /**
     * 获取今天
     *
     * @return
     */
    public Date getToday() {
        Date date = new java.sql.Date(new Date().getTime());
        return date;
    }

    /**
     * 获取昨天
     *
     * @return
     */
    public Date getYesterday() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(GregorianCalendar.DATE, -1);
        Date date = new java.sql.Date(calendar.getTime().getTime());
        return date;
    }

    /**
     * 获取上一周的第一天
     *
     * @return
     */
    public Date getLastWeekFristDay() {
        GregorianCalendar calendar = new GregorianCalendar();
        int minus = calendar.get(GregorianCalendar.DAY_OF_WEEK) - 1;
        if (minus == 0) minus = 7;
        calendar.add(GregorianCalendar.DATE, -6 - minus);
        Date date = new java.sql.Date(calendar.getTime().getTime());
        return date;
    }

    /**
     * 获取上一周的最后一天
     *
     * @return
     */
    public Date getLastWeekLastDay() {
        GregorianCalendar calendar = new GregorianCalendar();
        int minus = calendar.get(GregorianCalendar.DAY_OF_WEEK) - 1;
        if (minus == 0) minus = 7;
        calendar.add(GregorianCalendar.DATE, -minus);
        Date date = new java.sql.Date(calendar.getTime().getTime());
        return date;
    }

    /**
     * 获取本周第一天
     *
     * @return
     */
    public Date getThisWeekFristDay() {
        GregorianCalendar calendar = new GregorianCalendar();
        int minus = calendar.get(GregorianCalendar.DAY_OF_WEEK) - 2;
        if (minus < 0) {
            minus = 6;
        }
        calendar.add(GregorianCalendar.DATE, -minus);
        Date date = new java.sql.Date(calendar.getTime().getTime());
        return date;
    }

    /**
     * 本周最后一天
     *
     * @return
     */
    public Date getThisWeekLastDay() {
        return this.getToday();
    }

    /**
     * 上月第一天
     *
     * @return
     */
    public Date getLastMonthFristDay() throws ParseException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(calendar.get(GregorianCalendar.YEAR), calendar.get(GregorianCalendar.MONTH), 1);
        calendar.add(GregorianCalendar.DATE, -1);

        int month = calendar.get(GregorianCalendar.MONTH) + 1;
        String _month = month > 9 ? "" + month : "0" + month;
        String time = calendar.get(GregorianCalendar.YEAR) + "-" + _month + "-01 00:00:00";

        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = format.parse(time);
        return date;
    }

    /**
     * 上月最后一天
     *
     * @return
     */
    public Date getLastMonthLastDay() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(calendar.get(GregorianCalendar.YEAR), calendar.get(GregorianCalendar.MONTH), 1, 23, 59, 59);
        calendar.add(GregorianCalendar.DATE, -1);
        Date date = new java.sql.Date(calendar.getTime().getTime());
        return date;
    }

    /**
     * 获取这个月第一天
     *
     * @return
     */
    public Date getThisMonthFristDay() {
        GregorianCalendar calendar = new GregorianCalendar();
        int dayOfMonth = calendar.get(GregorianCalendar.DATE);
        calendar.add(GregorianCalendar.DATE, -dayOfMonth + 1);
        Date date = new java.sql.Date(calendar.getTime().getTime());
        return date;
    }

    /**
     * 获取这个月最后一天
     *
     * @return
     */
    public Date getThisMonthLastDay() {
        return this.getToday();
    }

    //--------------时间格式转换
    public Calendar strToCalendar(String date, String pattern) throws ParseException {
        Date dt = strToDate(date, pattern);
        return dateToCalendar(dt);
    }

    public Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }

    /**
     * @param dateStr String类型的时间
     * @param pattern String类型的时间的格式
     * @return
     * @throws ParseException
     */
    public Date strToDate(String dateStr, String pattern) throws ParseException {
        // String pattern="yyyy-MM-dd";
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = dateFormat.parse(dateStr);
        return date;
    }

    /**
     * @param date    date格式的时间
     * @param pattern 要转换的格式
     * @return
     */
    public String dateToStr(Date date, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        String dateStr = dateFormat.format(date);
        return dateStr;
    }
}
