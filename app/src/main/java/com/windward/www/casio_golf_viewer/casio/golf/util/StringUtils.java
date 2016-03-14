package com.windward.www.casio_golf_viewer.casio.golf.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

/**
 * 字符串操作工具包
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static ThreadLocal<DecimalFormat> priceFormat = new ThreadLocal<DecimalFormat>() {

        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("###,###,##0.00");
        }

    };
    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    private final static ThreadLocal<SimpleDateFormat> dateFormater3 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater5 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater6 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy年MM月dd日HH点mm分");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateChar = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater4 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy/MM/dd");
        }
    };

    public static int getTextHeight(Paint paint) {
        FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int height = Math.abs(fontMetrics.bottom - fontMetrics.top);
        return height;
    }

    /**
     * @param obj 数字类型
     * @return #, ###.#
     */
    public static String formatNum(Object obj) {
        try {
            DecimalFormat df = new DecimalFormat("#,###.#");
            return df.format(obj);
        } catch (Exception e) {
            return "0";
        }

    }

    public static String formatPrice(Double price) {
        return priceFormat.get().format(price);
    }

    public static String formatPrice(String price) {
        try {
            Double price_double = Double.parseDouble(price);
            return formatPrice(price_double);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 身份证中间数字替换
     */
    public static String coverIdCard(String id) {
        if (id.length() < 15) {
            return id;
        }
        String start = id.substring(0, 3);
        String end = id.substring(id.length() - 4, id.length());
        return start + " *** *** " + end;
    }

    /**
     * 护照中间数字替换
     */
    public static String coverPassport(String passport) {
        String start = passport.substring(0, 1);
        String end = passport.substring(passport.length() - 4, passport.length());
        return start + " *** " + end;
    }

    public static String getShowTime(String start, String arrive) {
        if (start.substring(0, 10).equals(arrive.substring(0, 10))) {
            return arrive.substring(11, 16) + "到达";
        } else {
            Calendar cal_start = Calendar.getInstance();
            cal_start.setTime(toDate(start));
            cal_start.add(Calendar.DATE, 1);
            Date start_date = cal_start.getTime();
            start = dateFormater.get().format(start_date);
            if (start.substring(0, 10).equals(arrive.substring(0, 10)))
                return "次日" + arrive.substring(11, 16) + "到达";
            return arrive.substring(5, 16).replace("-", "/") + "到达";

        }
    }

    /**
     * 获取图文信息(图片在文字的最前方)
     *
     * @param context
     * @param content
     * @param resId   图片的id
     * @return
     * @author fighter <br />
     * 创建时间:2013-8-9<br />
     * 修改时间:<br />
     */
    public static SpannableString getSpannableString(Context context,
                                                     String content, int resId) {
        SpannableString spannableString = new SpannableString("1" + content); // 1
        // 为占位符

        Drawable drawable = context.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(drawable);
        spannableString.setSpan(span, 0, 1,
                SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static String getCurrTime2() {
        return dateFormater2.get().format(new Date());
    }

    public static String getCurrTime() {
        return dateFormater.get().format(new Date());
    }

    public static String getCurrTime3() {
        return dateFormater3.get().format(new Date());
    }

    public static Date toDate(String pattern, String date) {
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static long getTime(String time, int type) {
        switch (type) {
            case 0:
                try {
                    return dateFormater.get().parse(time).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    return dateFormater3.get().parse(time).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
        return 0;
    }

    /**
     * timeEnd(判断是否是已经过时的时间)
     *
     * @param time
     * @return boolean true 过去时
     * @Title: timeEnd
     */
    public static boolean timeEnd(String time) {
        try {
            Date date = dateFormater.get().parse(time);
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            if (today.before(date)) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            return true;
        }
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * @param time
     * @return
     */
    public static long phpToJavaTime(String time) {
        long t = toLong(time);
        if (t <= 0) {
            return System.currentTimeMillis();
        }
        t *= 1000;
        return t;
    }

    public static String formatDateByLinux(String time) {
        return formatDateByLinux(toLong(time));
    }

    public static String formatDateByLinux(long currTime) {
        if (currTime <= 0) {
            return "无";
        }
        currTime *= 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currTime);
        return dateChar.get().format(calendar.getTime());
    }

    public static long formatDateByLinuxToLong(String time) {
        long currTime = toLong(time);
        if (currTime <= 0) {
            return System.currentTimeMillis();
        }
        currTime *= 1000;
        return currTime;
    }

    public static String formatDate(String date) {
        return formatDate(toDate(date).getTime(), 2);
    }

    public static String formatDate(String date, int type) {
        switch (type) {
            case 0:
                return dateFormater.get().format(toDate(date).getTime());
            case 2:
                return dateFormater2.get().format(toDate(date).getTime());
            case 3:
                return dateFormater3.get().format(toDate(date).getTime());
            case 4:
                return dateFormater4.get().format(toDate(date).getTime());
            case 5:
                return dateFormater5.get().format(toDate(date).getTime());
            case 6:
                return dateFormater6.get().format(toDate(date).getTime());
        }
        return null;
    }

    public static String getNow(int type) {
        return StringUtils.formatDate(new Date().getTime(), type);
    }

    /**
     * @param currTime
     * @param tag      0 日期2013-05-09，
     *                 1 日期加 时间,2013-12-25 12:25:22
     *                 2 2015年11月19日 17:01:00
     *                 3 日期2013/05/09
     * @return
     * @author fighter <br />
     * 创建时间:2013-8-12<br />
     * 修改时间:<br />
     */
    public static String formatDate(long currTime, int tag) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currTime);
//        if (1 == tag) {
//            return dateFormater.get().format(calendar.getTime());
//        } else {
//            return dateFormater2.get().format(calendar.getTime());
//        }
        switch (tag) {
            case 0:
                return dateFormater2.get().format(calendar.getTime());
            case 1:
                return dateFormater.get().format(calendar.getTime());
            case 2:
                return dateFormater5.get().format(calendar.getTime());
            case 3:
                return dateFormater4.get().format(calendar.getTime());
            default:
                return dateFormater3.get().format(calendar.getTime());
        }

    }

    public static String friendTime(String pattern, String date) {
        Date time = toDate(pattern, date);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
                if ("1分钟前".equals(ftime)) {
                    ftime = "刚刚";
                }
            } else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        System.out.println(days);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String friendTime(String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
                if ("1分钟前".equals(ftime)) {
                    ftime = "刚刚";
                }
            } else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else {
            ftime = dateFormater2.get().format(time);
        }
        // } else if (days == 1) {
        // ftime = "昨天";
        // } else if (days == 2) {
        // ftime = "前天";
        // } else if (days > 2 && days <= 10) {
        // ftime = days + "天前";
        // } else if (days > 10) {
        // ftime = dateFormater2.get().format(time);
        // }
        return ftime;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return (int) (Float.parseFloat(str) + 0.5f);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 格式化手机号,中间四位为* 手机号必须为11 位
     *
     * @param phone
     * @return
     */
    public static String formatPhone(String phone) {
        if (phone.length() == 11) {
            phone = String.format("%s****%s", phone.substring(0, 3),
                    phone.substring(7));
        }
        return phone;
    }

    /**
     * 效验电话号码是否输入合法
     *
     * @param phone
     * @return 合法手机号为 true
     * @author fighter <br />
     * 创建时间:2013-8-7<br />
     * 修改时间:<br />
     */
    public static boolean validatePhone(String phone) {
        boolean flag = false;
        if (phone.matches("^(1)\\d{10}$")) {
            flag = true;
        }
        return flag;
    }

    /**
     * 验证身份证号码
     *
     * @param card
     * @return 合法为true
     */
    public static boolean validateCardNo(String card) {
        boolean flag = false;
        if (card.matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)")) {
            flag = true;
        }
        return flag;
    }

    /**
     * 取银行卡后面4位
     *
     * @param account
     * @return
     */
    public static String formatAccount(String account) {
        if (TextUtils.isEmpty(account)) {
            return "";
        }
        String bankNum = account;
        int bankLen = bankNum.length();
        if (bankLen > 4) {
            bankNum = bankNum.substring(bankLen - 4, bankLen);
        }
        return bankNum;
    }

    /**
     * 银行用户 去姓
     *
     * @param name
     * @return
     */
    public static String formatBankAccountName(String name) {
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        name = name.replaceFirst(name.substring(0, 1), "*");
        return name;
    }

    public static double toDouble(String price) {
        double d = 0.0d;
        try {
            d = Double.parseDouble(price);
        } catch (Exception e) {
        }
        return d;
    }

    /**
     * 判断数组是否相邻
     *
     * @param string
     * @return 全部相邻 true isUpon("5,4,6,0")
     */
    public static boolean isUpon(String string) {
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        String[] strs = string.split(",");
        if (strs.length <= 1) {
            return false;
        }
        Arrays.sort(strs);
        int[] array = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            array[i] = Integer.parseInt(strs[i]);
            if (i > 0 && Math.abs(array[i] - array[i - 1]) != 1) {
                return false;
            }
        }
        return true;
    }

    public static float toFloat(String input) {
        try {
            return Float.parseFloat(input);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 复制到剪贴板管理器
     */
    public static void copy(String content, Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 从剪贴板管理器粘贴
     */
    public static String paste(Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }
}
