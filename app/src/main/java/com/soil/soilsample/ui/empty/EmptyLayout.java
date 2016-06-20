package com.soil.soilsample.ui.empty;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soil.soilsample.R;
import com.soil.soilsample.support.util.TDevice;


/**
 * Created by GIS on 2016/4/14 0014.
 */
public class EmptyLayout extends LinearLayout implements View.OnClickListener{
    public static final int HIDE_LAYOUT = 4;
    public static final int NETWORK_ERROR = 1;
    public static final int NETWORK_LOADING = 2;
    public static final int NODATA = 3;
    public static final int NODATA_ENABLE_CLICK = 5;
    private Boolean clickEnable = true;
    private android.view.View.OnClickListener listener;
    private int mErrorState;
    private RelativeLayout mLayout;
    private ProgressBar animProgress;
    private ImageView imgError;
    private TextView tvError;
    private final Context context;
    public EmptyLayout(Context context)
    {
        super(context);
        this.context = context;
        init();
    }
    public EmptyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }
    private void init()
    {
        View view = View.inflate(context, R.layout.view_error_layout, null);
        mLayout = (RelativeLayout) view.findViewById(R.id.page_error_layout);
        animProgress = (ProgressBar) view.findViewById(R.id.animProgress);
        imgError = (ImageView) view.findViewById(R.id.img_error_layout);
        tvError = (TextView) view.findViewById(R.id.tv_error_layout);
        setOnClickListener(this);
        imgError.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickEnable)
                {
                    if (listener != null)
                    {
                        listener.onClick(v);
                    }
                }
            }
        });
        addView(view);
    }
    public void dismiss()
    {
        mErrorState = HIDE_LAYOUT;
        setVisibility(View.GONE);
    }
    public int getErrorState() {
        return mErrorState;
    }

    public boolean isLoadError() {
        return mErrorState == NETWORK_ERROR;
    }

    public boolean isLoading() {
        return mErrorState == NETWORK_LOADING;
    }
    public void setErrorMessage(String msg)
    {
        tvError.setText(msg);
    }
    public void setErrorType(int i)
    {
        setVisibility(View.VISIBLE);
        switch (i)
        {
            case NETWORK_ERROR:
                mErrorState = NETWORK_ERROR;
                if (TDevice.hasInternet())
                {
                    tvError.setText(R.string.error_view_load_error_click_to_refresh);
                    imgError.setBackgroundResource(R.drawable.pagefailed_bg);
                }else
                {
                    tvError.setText(R.string.error_view_network_error_click_to_refresh);
                    imgError.setBackgroundResource(R.drawable.page_icon_network);
                }
                imgError.setVisibility(View.VISIBLE);
                animProgress.setVisibility(View.GONE);
                clickEnable = true;
                break;
            case NETWORK_LOADING:
                mErrorState = NETWORK_LOADING;

                animProgress.setVisibility(View.VISIBLE);
                imgError.setVisibility(View.GONE);
                tvError.setText(R.string.error_view_loading);
                clickEnable = false;
                break;
            case NODATA:
                mErrorState = NODATA;

                imgError.setBackgroundResource(R.drawable.page_icon_empty);
                imgError.setVisibility(View.VISIBLE);
                animProgress.setVisibility(View.GONE);
                setNoDataContent();
                clickEnable = true;
                break;
            case HIDE_LAYOUT:
                setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public void setNoDataContent()
    {
        tvError.setText(R.string.error_view_no_data);
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.GONE)
            mErrorState = HIDE_LAYOUT;
        super.setVisibility(visibility);
    }
    public void setOnLayoutClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
    @Override
    public void onClick(View v) {
        if (clickEnable)
        {
            if (listener != null)
            {
                listener.onClick(v);
            }
        }
    }
}
