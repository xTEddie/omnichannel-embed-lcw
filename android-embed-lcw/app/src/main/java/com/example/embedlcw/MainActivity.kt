package com.example.embedlcw

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.DownloadListener
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import java.net.URL


class MainActivity : AppCompatActivity() {
    var debug = true
    var uploadedFileTempPath: ValueCallback<Array<Uri?>?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request permissions
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PackageManager.PERMISSION_GRANTED
        );

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

        wv.webChromeClient = object: WebChromeClient() {

            // Handles HTML forms with 'file' input type on android API 21+
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri?>?>,
                fileChooserParams: FileChooserParams
            ): Boolean {

                if (Build.VERSION.SDK_INT >= 21) {
                    uploadedFileTempPath = filePathCallback
                    val intent = fileChooserParams.createIntent()
                    try {
                        startActivityForResult(intent, 100) // Send request code for select file
                    } catch (e: Exception) {
                        Toast.makeText(
                            applicationContext,
                            "Unable to open file chooser",
                            Toast.LENGTH_LONG
                        ).show()
                        return false
                    }

                    return true
                }

                return false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Build.VERSION.SDK_INT >= 21) {

            if (resultCode == Activity.RESULT_CANCELED) {
                uploadedFileTempPath?.onReceiveValue(null) // Need to send null value to ensure future attempts workable
                return
            }

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == 100) {
                    uploadedFileTempPath?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                    uploadedFileTempPath = null
                }
            }
        }
    }
}