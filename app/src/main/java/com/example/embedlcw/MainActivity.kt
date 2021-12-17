package com.example.embedlcw

import android.app.Application
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.*


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

        wv.webViewClient = object: WebViewClient() {

            /**
             * Intercepts resources and load them locally.
             */
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                try {
                    if (request?.url?.lastPathSegment == "webchat-es5.js" && request?.url?.toString()!!.contains("4.9.2")) { // 766 kB
                        return WebResourceResponse(
                            "text/javascript",
                            "gzip",
                            application.assets.open("botframework-webchat/4.9.2/webchat-es5.js")
                        )
                    }

                    if (request?.url?.lastPathSegment == "jquery-3.4.1.min.js") { // 33.2 kB
                        return WebResourceResponse(
                            "text/javascript",
                            "gzip",
                            application.assets.open("jquery-3.4.1.min.js")
                        )
                    }
                } catch (e: Exception) {
                    return super.shouldInterceptRequest(view, request)
                }
                return super.shouldInterceptRequest(view, request)
            }
        }

        wv.loadUrl("file:///android_asset/index.html")
    }
}