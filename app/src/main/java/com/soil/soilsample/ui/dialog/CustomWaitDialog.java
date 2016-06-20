package com.soil.soilsample.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.soil.soilsample.R;

/**
 * Created by GIS on 2016/6/20 0020.
 * 自定义加载等待对话框
 */
public class CustomWaitDialog extends Dialog {

    public CustomWaitDialog(Context context) {
        super(context);
    }
    /**
     * 当窗口焦点改变时调用
     */
    public CustomWaitDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView dialogImg = (ImageView) findViewById(R.id.img_wait_dialog);
        // 获取ImageView上的动画背景
        AnimationDrawable spinner = (AnimationDrawable) dialogImg.getBackground();
        // 开始动画
        spinner.start();
    }

    /**
     * @param message 给Dialog设置提示信息
     */
    public void setDialogMessage(CharSequence message)
    {
        if (message != null && message.length() >0 )
        {
            TextView messageText = (TextView) findViewById(R.id.tv_dialog_message);
            messageText.setText(message);
            messageText.setVisibility(View.VISIBLE);
            messageText.invalidate();
        }
    }
    /**
     * 弹出自定义ProgressDialog
     *
     * @param context
     *            上下文
     * @param message
     *            提示
     * @param cancelable
     *            是否按返回键取消
     * @param cancelListener
     *            按下返回键监听
     * @return
     */
    public static CustomWaitDialog show(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener)
    {
        CustomWaitDialog dialog = new CustomWaitDialog(context, R.style.Custom_WaitDialog);
        dialog.setTitle("");
        dialog.setContentView(R.layout.custom_waitdialog);
        if (message == null && message.length() == 0 )
        {
            dialog.findViewById(R.id.tv_dialog_message).setVisibility(View.GONE);
        }else
        {
            /*TextView messageTxt = (TextView) dialog.findViewById(R.id.tv_dialog_message);
            messageTxt.setText(message);*/
            TextView messageText = (TextView) dialog.findViewById(R.id.tv_dialog_message);
            messageText.setText(message);
            messageText.setVisibility(View.VISIBLE);
            messageText.invalidate();
        }
        // 按返回键是否取消
        dialog.setCancelable(cancelable);
        //点击屏幕其他位置，对话框不消失
        dialog.setCanceledOnTouchOutside(false);
        // 监听返回键处理
        dialog.setOnCancelListener(cancelListener);
        // 设置居中
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        // 设置背景层透明度
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();
        return dialog;
    }
}
