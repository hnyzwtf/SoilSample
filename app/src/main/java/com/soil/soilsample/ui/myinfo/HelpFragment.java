package com.soil.soilsample.ui.myinfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.soil.soilsample.R;

/**
 * Created by GIS on 2016/8/4 0004.
 */
public class HelpFragment extends Fragment {
    private String mUrl;
    public static HelpFragment newInstance(String url)
    {
        Bundle bundle = new Bundle();
        bundle.putString("help_url", url);
        HelpFragment fragment = new HelpFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString("help_url");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        WebView webView = (WebView) view.findViewById(R.id.webview_help);
        webView.loadUrl(mUrl);
        return view;
    }
}
