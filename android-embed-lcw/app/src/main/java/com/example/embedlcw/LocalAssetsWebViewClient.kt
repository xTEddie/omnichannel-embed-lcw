package com.example.embedlcw

import android.opengl.Visibility
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.InputStream

class LocalAssetsWebViewClient(private var wv: WebView) : WebViewClient() {
    private var preload = false; // Use preloaded assets on init instead of at runtime
    private var useLocalAssets = false;
    private lateinit var localAssets: Map<String, InputStream>;

    init {
        Log.i("LocalAssetsWebViewClient", "Init")
        val application = wv.context.applicationContext
        if (preload) {
            localAssets = mapOf(
                "webchat-es5.js" to application.assets.open("botframework-webchat/4.9.2/webchat-es5.js"),
                "chat-adapter-0.0.35-beta.1.js" to application.assets.open("chat-adapter-0.0.35-beta.1.js"),
                "webchat-ic3adapter/0.1.0-master.2dba07b/botframework-webchat-adapter-ic3.production.min.js" to application.assets.open(
                    "webchat-ic3adapter/0.1.0-master.2dba07b/botframework-webchat-adapter-ic3.production.min.js"
                ),
                "jquery-3.4.1.min.js" to application.assets.open("jquery-3.4.1.min.js"),
                "ocsdk/0.2.1-main.d3114ea/SDK.min.js" to application.assets.open("ocsdk/0.2.1-main.d3114ea/SDK.min.js"),
                "purify.min.js" to application.assets.open("purify.min.js"),
                "ams/0.1.0-main.ef0152f/SDK.min.js" to application.assets.open("ams/0.1.0-main.ef0152f/SDK.min.js"),
                "appinsights.js" to application.assets.open("appinsights.js")
            )
        }
    }

    /**
     * Intercepts resources and load them from local assets.
     *
     * Use at your own risk: Local assets may not be in sync with the production assets.
     */
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        if (useLocalAssets) {
            val application = view.context.applicationContext

            try {
                if (request?.url?.lastPathSegment == "webchat-es5.js" && request?.url?.toString()!!
                        .contains("4.9.2")
                ) { // 766 kB
                    Log.i("LocalAssetsWebViewClient", "Intercept webchat-es5.js")
                    return WebResourceResponse(
                        "text/javascript",
                        "gzip",
                        if (preload) localAssets["webchat-es5.js"] else application.assets.open("botframework-webchat/4.9.2/webchat-es5.js")
                    )
                }

                if (request?.url?.lastPathSegment == "chat-adapter-0.0.35-beta.1.js") { // 119 kB
                    return WebResourceResponse(
                        "text/javascript",
                        "gzip",
                        if (preload) localAssets["chat-adapter-0.0.35-beta.1.js"] else application.assets.open("chat-adapter-0.0.35-beta.1.js")
                    )
                }

                if (request?.url?.lastPathSegment == "botframework-webchat-adapter-ic3.production.min.js" && request?.url?.toString()!!
                        .contains("0.1.0-master.2dba07b")
                ) { // 118 kB
                    return WebResourceResponse(
                        "text/javascript",
                        "gzip",
                        if (preload) localAssets["webchat-ic3adapter/0.1.0-master.2dba07b/botframework-webchat-adapter-ic3.production.min.js"] else application.assets.open("webchat-ic3adapter/0.1.0-master.2dba07b/botframework-webchat-adapter-ic3.production.min.js")
                    )
                }

                if (request?.url?.lastPathSegment == "jquery-3.4.1.min.js") { // 33.2 kB
                    return WebResourceResponse(
                        "text/javascript",
                        "gzip",
                        if (preload) localAssets["jquery-3.4.1.min.js"] else application.assets.open("jquery-3.4.1.min.js")
                    )
                }

                if (request?.url?.lastPathSegment == "SDK.min.js" && request?.url?.toString()!!
                        .contains("ocsdk") && request?.url?.toString()!!
                        .contains("0.2.1-main.d3114ea")
                ) { // 18.4 kB
                    return WebResourceResponse(
                        "text/javascript",
                        "gzip",
                        if (preload) localAssets["ocsdk/0.2.1-main.d3114ea/SDK.min.js"] else application.assets.open("ocsdk/0.2.1-main.d3114ea/SDK.min.js")
                    )
                }

                if (request?.url?.lastPathSegment == "purify.min.js") { // 7.2 kB
                    return WebResourceResponse(
                        "text/javascript",
                        "gzip",
                        if (preload) localAssets["purify.min.js"] else application.assets.open("purify.min.js")
                    )
                }

                if (request?.url?.lastPathSegment == "SDK.min.js" && request?.url?.toString()!!
                        .contains("ams") && request?.url?.toString()!!
                        .contains("0.1.0-main.ef0152f")
                ) { // 5.2 kB
                    return WebResourceResponse(
                        "text/javascript",
                        "gzip",
                        if (preload) localAssets["ams/0.1.0-main.ef0152f/SDK.min.js"] else application.assets.open("ams/0.1.0-main.ef0152f/SDK.min.js")
                    )
                }

                if (request?.url?.lastPathSegment == "appinsights.js") { // 3.0 kB
                    return WebResourceResponse(
                        "text/javascript",
                        "gzip",
                        if (preload) localAssets["appinsights.js"] else application.assets.open("appinsights.js")
                    )
                }
            } catch (e: Exception) {
                return super.shouldInterceptRequest(view, request)
            }
        }

        return super.shouldInterceptRequest(view, request)
    }
}