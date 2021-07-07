package com.example.eaduan;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private ValueCallback<Uri[]> afterLolipop;
    private ValueCallback<Uri> mUploadMessage;

    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }



        });

        myWebView.setWebChromeClient(new WebChromeClient(){

            // For Android 3.0+ - undocumented method
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), 101);
                Log.i("DEBUG", "Open file Chooser");
                mUploadMessage = uploadMsg;
            }

            // For Android > 4.1 - undocumented method
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 101);

            }

            // For Android > 5.0
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

                startActivityForResult(fileChooserParams.createIntent(), 101);


                return false;
            }
        });
        myWebView.loadUrl("http://52.221.235.78/mbsp");

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 101:
                if (resultCode == RESULT_OK) {

                    Uri result = intent == null || resultCode != RESULT_OK ? null
                            : intent.getData();
                    if (mUploadMessage != null) {
                        mUploadMessage.onReceiveValue(result);
                    } else if (afterLolipop != null) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            afterLolipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                            afterLolipop = null;
                        }
                    }
                    mUploadMessage = null;
                }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (myWebView.canGoBack()) {
                        myWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
