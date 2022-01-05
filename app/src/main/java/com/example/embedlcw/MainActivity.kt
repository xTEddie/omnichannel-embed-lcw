package com.example.embedlcw

import android.app.Application
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.*
import java.io.File
import java.net.URL


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
//        wv.settings.cacheMode = WebSettings.LOAD_DEFAULT

//        wv.webChromeClient = WebChromeClient()

//        var url = URL(OmnichannelConfig.config["bootstrapper"])
//        val baseUrl = "${url.protocol}://${url.host}"
//
//        val data = application.assets.open("index.html").bufferedReader().use {
//            it.readText()
//        };

//        wv.loadDataWithBaseURL(baseUrl, data, "text/html", null, baseUrl)

        val queryParams = "orgId=${OmnichannelConfig.config["orgId"]}&orgUrl=${OmnichannelConfig.config["orgUrl"]}&appId=${OmnichannelConfig.config["appId"]}&hideChatbutton=${OmnichannelConfig.config["hideChatbutton"]}&renderMobile=${OmnichannelConfig.config["renderMobile"]}&src=${OmnichannelConfig.config["src"]}"
        wv.loadUrl("${OmnichannelConfig.config["webPage"]}?${queryParams}")
    }
}