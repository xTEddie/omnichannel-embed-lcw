package com.example.embedlcw

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.DownloadListener
import android.webkit.WebView
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

        // Load local .html with baseUrl set to LCW production domain since attachment downloads does not work cross-origin
        val queryParams = "orgId=${OmnichannelConfig.config["orgId"]}&orgUrl=${OmnichannelConfig.config["orgUrl"]}&appId=${OmnichannelConfig.config["appId"]}&hideChatbutton=${OmnichannelConfig.config["hideChatbutton"]}&renderMobile=${OmnichannelConfig.config["renderMobile"]}&src=${OmnichannelConfig.config["src"]}"
        var url = URL(OmnichannelConfig.config["src"])
        val baseUrl = "${url.protocol}://${url.host}?${queryParams}"
        val data = application.assets.open("index.html").bufferedReader().use {
            it.readText()
        };

        wv.loadDataWithBaseURL(baseUrl, data, "text/html", null, baseUrl)

        // Expose Android methods to Javascript layer
        val javascriptInterface = JavascriptInterface(applicationContext)
        wv.addJavascriptInterface(javascriptInterface, "Android")

        // Subscribe to notification when a file from Web content needs to be downloaded in Android layer
        wv.setDownloadListener(DownloadListener { url, _, _, _, _ ->
            if (url.startsWith("blob:")) {
                wv.evaluateJavascript(javascriptInterface.getBase64StringFromBlobUrl(url), null)
            }
        })
    }
}