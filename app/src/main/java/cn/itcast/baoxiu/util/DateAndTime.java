package cn.itcast.baoxiu.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateAndTime {
    @SuppressLint("SetTextI18n")
    public static void showDatePickerDialog(Activity activity, final TextView tv) {
        // Calendar 需要这样来得到
        Calendar calendar = Calendar.getInstance();
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(activity,
                // 绑定监听器(How the parent is notified that the date is set.)
                (view, year, monthOfYear, dayOfMonth) -> {
                    String timeStr = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                    String date = null;
                    try {
                        date = getTransDate(timeStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    tv.setText(date);
                }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public static void showTimePickerDialog(Activity activity, final TextView tv) {
        Calendar calendar = Calendar.getInstance();
        // 创建一个TimePickerDialog实例，并把它显示出来
        new TimePickerDialog(activity,
                // 绑定监听器
                (view, hourOfDay, minute) -> {
                    String timeStr = hourOfDay+":"+minute+":"+Calendar.getInstance().get(Calendar.SECOND);
                    String date = null;
                    try {
                        date = getTransTime(timeStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    tv.append("T"+date);
                }
                // 设置初始时间
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                // true表示采用24小时制
                , true).show();
    }

    public static String getTransDate(String timeStr) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(timeStr);
        assert date != null;
        return simpleDateFormat.format(date);
    }

    public static String getTransTime(String timeStr) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = simpleDateFormat.parse(timeStr);
        assert date != null;
        return simpleDateFormat.format(date);
    }
}
