package com.nanodegree.movietime.features;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.nanodegree.movietime.R;
import com.nanodegree.movietime.util.MyWebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nanodegree.movietime.util.Contracts.REVIEW_URL;

public class WebActivity extends AppCompatActivity {
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet);
        ButterKnife.bind(this);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
        Intent fromDetailActivity = getIntent();
        if (fromDetailActivity.hasExtra(REVIEW_URL)){
            String webUrl = fromDetailActivity.getStringExtra(REVIEW_URL);
            webView.loadUrl(webUrl);
            getSupportActionBar().setTitle(webUrl);
            toolbar.setTitle(webUrl);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
