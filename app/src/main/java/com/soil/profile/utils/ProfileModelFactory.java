package com.soil.profile.utils;

import android.app.Activity;

import com.soil.profile.model.InitProfileModel.MontainYelloSoilOne;

/**
 * Created by GIS on 2016/6/28 0028.
 */
public class ProfileModelFactory {
    public static MontainYelloSoilOne getMontainYelloSoilOne(Activity activity, int viewId) {
        return (MontainYelloSoilOne) activity.findViewById(viewId);
    }
}
