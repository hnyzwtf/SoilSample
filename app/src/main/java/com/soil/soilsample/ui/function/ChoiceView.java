package com.soil.soilsample.ui.function;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.soil.soilsample.R;


/**
 * 实现具有单选功能的listview，参考博客：http://blog.csdn.net/maosidiaoxian/article/details/45867927
 */
public class ChoiceView extends LinearLayout implements Checkable{
    private TextView mTextView;
    private RadioButton mRadioButton;
    public ChoiceView(Context context)
    {
        super(context);
        View.inflate(context, R.layout.item_single_choice, this);
        mTextView = (TextView) findViewById(R.id.tv_sample_model);
        mRadioButton = (RadioButton) findViewById(R.id.checkedView);
    }
    public void setText(String text)
    {
        mTextView.setText(text);
    }

    @Override
    public void setChecked(boolean checked) {
        mRadioButton.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return mRadioButton.isChecked();
    }

    @Override
    public void toggle() {
        mRadioButton.toggle();
    }
}
