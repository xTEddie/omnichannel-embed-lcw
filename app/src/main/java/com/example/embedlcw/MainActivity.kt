package com.example.embedlcw

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView


class MainActivity : AppCompatActivity() {
    var debug = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (debug) {
            WebView.setWebContentsDebuggingEnabled(debug)
        }

        val wv = findViewById<WebView>(R.id.web_view)
        wv.settings.javaScriptEnabled = true
        wv.settings.domStorageEnabled = true

//        wv.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
//        wv.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//        wv.webChromeClient = WebChromeClient()

        wv.loadUrl("file:///android_asset/index.html")
    }
}