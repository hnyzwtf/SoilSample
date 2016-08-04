package com.soil.soilsample.support.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.soil.soilsample.ui.myinfo.HelpFragment;

/**
 * Created by GIS on 2016/8/4 0004.
 */
public class HelpFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"应用简介","如何使用"};
    private Context mContext;
    private String url;
    public HelpFragmentPagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        this.mContext = context;
    }
    @Override
    public Fragment getItem(int position) {
        if (position == 0)
        {
            url = "file:///android_asset/app_about.html";
        }
        if (position == 1)
        {
            url = "file:///android_asset/app_help.html";
        }
        return HelpFragment.newInstance(url);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
