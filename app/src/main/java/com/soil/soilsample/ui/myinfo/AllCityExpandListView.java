package com.soil.soilsample.ui.myinfo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * 自定义expandlistview，用于显示所有城市列表，因当expandlistview与listview在本程序中同时使用时会导致每个控件只会显示一条数据
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
