package de.trilobytese.vocab.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import de.trilobytese.vocab.R;

public class HelpFragment extends Fragment {

    private static final String TAG = HelpFragment.class.getSimpleName();

    private WebView mWebView;
    private View mLayoutLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_help, container, false);
        mWebView = (WebView)rootView.findViewById(R.id.web_view);
        mLayoutLoading = rootView.findViewById(R.id.layout_loading);

        mWebView.setVisibility(View.GONE);
        mLayoutLoading.setVisibility(View.VISIBLE);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    mWebView.setVisibility(View.VISIBLE);
                    mLayoutLoading.setVisibility(View.GONE);
                }
            }
        });

        mWebView.clearCache(true);
        mWebView.loadUrl("file:///android_asset/help.html");
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(false);

        return rootView;
    }
}
