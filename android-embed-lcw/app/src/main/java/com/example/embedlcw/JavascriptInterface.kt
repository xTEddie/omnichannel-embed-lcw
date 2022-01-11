package com.example.embedlcw

import android.content.Context
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

// References:
// - https://stackoverflow.com/questions/48892390/download-blob-file-from-website-inside-android-webviewclient/48954970#48954970
// - https://github.com/JaegerCodes/AmazingAndroidWebview

class JavascriptInterface {
    var context: Context;

    constructor(context: Context) {
        this.context = context;
    }

    @JavascriptInterface
    fun showToast(toast: String?) {
        Toast.makeText(this.context, toast, Toast.LENGTH_SHORT).show()
    }

    /**
     * Handler to process Base64 data.
     *
     * 1. Strip Base64 prefix from Base64 data
     * 2. Decode Base64 data
     * 3. Write Base64 data to file based on mime type located in prefix
     * 4. Save file locally
     */
    @JavascriptInterface
    fun processBase64Data(base64Data: String) {
        Log.i("JavascriptInterface/processBase64Data", "Processing base64Data ...")

        var fileName = "";
        var bytes = "";

        if (base64Data.startsWith("data:image/png;base64,")) {
            fileName = "foo.png"
            bytes = base64Data.replaceFirst("data:image/png;base64,","")
        }

        if (fileName.isNotEmpty() && bytes.isNotEmpty()) {
            val downloadPath = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )

            Log.i("JavascriptInterface/processBase64Data", "Download Path: ${downloadPath.absolutePath}")

            val decodedString = Base64.decode(bytes, Base64.DEFAULT)
            val os = FileOutputStream(downloadPath, false)
            os.write(decodedString)
            os.flush()
        }
    }

    /**
     * Get Base 64 string from Blob URL in Android layer.
     *
     * 1. Download Blob URL as Blob object
     * 2. Convert Blob object to Base64 data
     * 3. Pass Base64 data to Android layer for processing
     */
    fun getBase64StringFromBlobUrl(blobUrl: String): String {
        Log.i("JavascriptInterface/getBase64StringFromBlobUrl", "Downloading $blobUrl ...")

        // Script to convert blob URL to Base64 data in Web layer, then process it in Android layer
        val script = "javascript: (() => {" +
            "async function getBase64StringFromBlobUrl() {" +
            "const xhr = new XMLHttpRequest();" +
            "xhr.open('GET', '${blobUrl}', true);" +
            "xhr.setRequestHeader('Content-type', 'image/png');" +
            "xhr.responseType = 'blob';" +
            "xhr.onload = () => {" +
            "if (xhr.status === 200) {" +
            "const blobResponse = xhr.response;" +
            "const fileReaderInstance = new FileReader();" +
            "fileReaderInstance.readAsDataURL(blobResponse);" +
            "fileReaderInstance.onloadend = () => {" +
            "console.log('Downloaded' + ' ' + '${blobUrl}' + ' ' + 'successfully!');" +
            "const base64data = fileReaderInstance.result;" +
            "Android.processBase64Data(base64data);" +
            "}" + // file reader on load end
            "}" + // if
            "};" + // xhr on load
            "xhr.send();" +
            "}" + // async function
            "getBase64StringFromBlobUrl();" +
            "}) ()"

        return script
    }
}