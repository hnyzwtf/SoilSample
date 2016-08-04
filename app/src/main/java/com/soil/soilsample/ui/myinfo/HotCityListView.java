package com.soil.soilsample.ui.myinfo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 自定义热门城市listview
 */
public class HotCityListView extends ListView {
    public HotCityListView(Context context) {
        super(context);
    }

    public HotCityListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HotCityListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
