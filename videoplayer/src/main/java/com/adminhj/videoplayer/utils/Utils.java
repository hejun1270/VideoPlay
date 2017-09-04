package com.adminhj.videoplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AdminHeJun on 2017/7/5.
 * </p>
 * Content:this is utility class
 * </p>
 * Modified:
 * </p>
 * Version:
 * </p>
 */

public class Utils {
    private static long lastTimeStamp = 0;
    private static long lastTotalRxBytes = 0;

    /**
     * get network speed call every two seconds
     *
     * @param context need to pass in the context
     * @return
     */
    public static String getNetSpeed(Context context) {
        String netSpeed = "0 kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//change to KB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        netSpeed = String.valueOf(speed) + " kb/s";
        return netSpeed;
    }

    /**
     * this method hides virtual keyboard
     * @param activity need to pass activity
     */
    public static void hideBottomUIMenu(Activity activity) {
        if (Build.VERSION.SDK_INT > 16 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;// hide status bar
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * long time type transformation string type ,format to 00:00.
     *
     * @param time need to pass in the long type data
     * @return time format 00:00
     */
    public static String durationFromat(long time) {

        if (time / 1000 < 60) {
            return String.format("%02d:%02d", 0, time / 1000);
        } else if (time / 1000 < 60 * 60) {
            time /= 1000;
            long minute = time / 60;
            long second = time % 60;
            return String.format("%02d:%02d", minute, second);
        } else if (time / 1000 >= 60 * 60) {
            time /= 1000;
            long hour = time / (60 * 60);
            long minute = (time % (60 * 60)) / 60;
            long second = (time % (60 * 60)) % 60;
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
        return "00:00";
    }

    /**
     * get system time
     *
     * @return
     */
    public static String getSystemTime() {
        Long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String currentTime = format.format(date);
        return currentTime;
    }


    /**
     * get screen height
     *
     * @return
     */
    public static int getScreenHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int height = dm.heightPixels;
        return height;
    }

    /**
     * Get screen width
     *
     * @return
     */
    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        return width;
    }

    /**
     * Determine whether wifi is connected
     *
     * @param mContext need to pass in the context
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
    /**
     * sd卡容量大小转换为string
     *
     * @param size
     * @return
     */
    public static String sdSizeFromat(Context context, long size) {
        return android.text.format.Formatter.formatFileSize(context, size);
    }
}
