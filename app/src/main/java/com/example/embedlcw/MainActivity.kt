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

                    if (request?.url?.lastPathSegment == "chat-adapter-0.0.35-beta.1.js") { // 119 kB
                        return WebResourceResponse(
                            "text/javascript",
                            "gzip",
                            application.assets.open("chat-adapter-0.0.35-beta.1.js")
                        )
                    }

                    if (request?.url?.lastPathSegment == "botframework-webchat-adapter-ic3.production.min.js" && request?.url?.toString()!!.contains("0.1.0-master.2dba07b")) { // 118 kB
                        return WebResourceResponse(
                            "text/javascript",
                            "gzip",
                            application.assets.open("webchat-ic3adapter/0.1.0-master.2dba07b/botframework-webchat-adapter-ic3.production.min.js")
                        )
                    }

                    if (request?.url?.lastPathSegment == "jquery-3.4.1.min.js") { // 33.2 kB
                        return WebResourceResponse(
                            "text/javascript",
                            "gzip",
                            application.assets.open("jquery-3.4.1.min.js")
                        )
                    }

                    if (request?.url?.lastPathSegment == "SDK.min.js" && request?.url?.toString()!!.contains("ocsdk") && request?.url?.toString()!!.contains("0.2.1-main.d3114ea")) { // 18.4 kB
                        return WebResourceResponse(
                            "text/javascript",
                            "gzip",
                            application.assets.open("ocsdk/0.2.1-main.d3114ea/SDK.min.js")
                        )
                    }

                    if (request?.url?.lastPathSegment == "purify.min.js") { // 7.2 kB
                        return WebResourceResponse(
                            "text/javascript",
                            "gzip",
                            application.assets.open("purify.min.js")
                        )
                    }

                    if (request?.url?.lastPathSegment == "SDK.min.js" && request?.url?.toString()!!.contains("ams") && request?.url?.toString()!!.contains("0.1.0-main.ef0152f")) { // 5.2 kB
                        return WebResourceResponse(
                            "text/javascript",
                            "gzip",
                            application.assets.open("ams/0.1.0-main.ef0152f/SDK.min.js")
                        )
                    }

                    if (request?.url?.lastPathSegment == "appinsights.js") { // 3.0 kB
                        return WebResourceResponse(
                            "text/javascript",
                            "gzip",
                            application.assets.open("appinsights.js")
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