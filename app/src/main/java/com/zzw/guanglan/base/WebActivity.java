package com.zzw.guanglan.base;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.zzw.guanglan.R;
import com.zzw.guanglan.utils.ToastUtils;

/**
 * Created by zzw on 2019/1/28.
 * 描述:
 */
public class WebActivity extends BaseActivity {
    private ProgressBar progressBar;
    public static String PATH = "path";
    public static String TITLE = "title";
    private String path;
    private WebView webView;

    public static void open(Context context, String title, String path) {
        context.startActivity(new Intent(context, WebActivity.class)
                .putExtra(TITLE, title)
                .putExtra(PATH, path)
        );
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected void initView() {

        String title = getIntent().getStringExtra(TITLE);
        path = getIntent().getStringExtra(PATH);

        setTitle(title);

        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");//设置字符集编码
        webView.getSettings().setJavaScriptEnabled(true);//支持javascript

        webView.setWebViewClient(new webViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == progressBar.getMax()) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == progressBar.getVisibility()) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }


    @Override
    protected void initData() {
        super.initData();

        if (path == null) {
            ToastUtils.showToast("地址错误");
            finish();
        }
        webView.loadUrl(path);
    }

    class webViewClient extends WebViewClient {
        //重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            //如果不需要其他对点击链接事件的处理返回true，否则返回false
            return true;
        }
    }
}
