package com.adminhj.videoplayer.wedigt;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.adminhj.videoplayer.R;

/**
 * Created by AdminHeJun on 2017/7/4.
 * </p>
 * Content:this is custom toast
 * </p>
 * Modified:
 * </p>
 * Version:
 * </p>
 */

public class CustomToast {

    private static CustomToast instance;

    private TextView mHint;
    private TextView mContent;

    private Toast mToast;

    private CustomToast(Context context) {
        //loading custom toast layout
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        //init control view
        mHint = (TextView) toastRoot.findViewById(R.id.hint_text);
        mContent = (TextView) toastRoot.findViewById(R.id.message);
        //init Toast
        mToast = new Toast(context);
        //get screen height
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        mToast.setGravity(Gravity.TOP, 0, height / 3);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(toastRoot);
    }

    public static CustomToast getInstance(Context context) {
        if (instance == null) {
            instance = new CustomToast(context);
        }
        return instance;
    }

    /**
     * show the toast
     * @param hint you need show title
     * @param message need show message
     */
    public void showToast(String hint, String message) {
        mHint.setText(hint);
        mContent.setText(message);
        mToast.show();
    }

    /**
     * cancel the toast show
     */
    public void cancelToast() {
        mToast.cancel();
    }
}
