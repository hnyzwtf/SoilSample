package com.soil.soilsample.base;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIS on 2016/5/2 0002.
 */
public class ActivityCollector {
    public static List<AppCompatActivity> activities = new ArrayList<AppCompatActivity>();
    public static void addActivity(AppCompatActivity activity)
    {
        activities.add(activity);
    }
    public static void removeActivity(AppCompatActivity activity)
    {
        activities.remove(activity);
    }
    public static void finishAll()
    {
        for (Activity activity:activities)
        {
            if (! activity.isFinishing())
            {
                activity.finish();
            }
        }
        System.exit(0);
    }
}
