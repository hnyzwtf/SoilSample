package com.soil.soilsample.ui.myinfo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by GIS on 2016/7/8 0008.
 */
public class AllCityExpandListView extends ExpandableListView {
    public AllCityExpandListView(Context context) {
        super(context);
    }

    public AllCityExpandListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AllCityExpandListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
