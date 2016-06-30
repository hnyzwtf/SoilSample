package com.soil.profile.ui;

import android.hardware.Sensor;
import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MyOrienListener implements SensorEventListener {

    private SensorManager mSensorManager;
    private Context context;
    private Sensor sensor;

    private float lastX;

    public MyOrienListener(Context context) {
        // TODO 自动生成的构造函数存根
        this.context = context;
    }

    public void start() {
        // 拿到系统服务,所有的系统服务获取方法都是用context.getSystemService方法实现，只是传入的参数不同
        mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            // 获得方向传感器
            sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        if (sensor != null) {
            mSensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {// 精度改变
        // TODO 自动生成的方法存根

    }

    @Override
    public void onSensorChanged(SensorEvent event) {// 方向发生变化
        // TODO 自动生成的方法存根
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float x = event.values[SensorManager.DATA_X];
            // 避免太快地更新UI，加入判断条件，如果变化大于1度的话，才进行UI更新
            if (Math.abs(x - lastX) > 1.0) {
                // 经过一个回调，通知主界面更新（即是说，"1号香锅好了，请过来拿"）
                if (mOnOrientationListener != null) {
                    mOnOrientationListener.onOrientationChanged(x);
                }
            }
            lastX = x;
        }
    }

    private OnOrientationListener mOnOrientationListener;

    public void setOnOrientationListener(
            OnOrientationListener mOnOrientationListener) {
        this.mOnOrientationListener = mOnOrientationListener;
    }

    public interface OnOrientationListener {
        void onOrientationChanged(float x);
    }
}

