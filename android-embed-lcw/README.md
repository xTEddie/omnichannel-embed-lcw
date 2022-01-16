# Android Embed LCW

Sample [Android](https://www.android.com/) app to embed [Dynamics 365 Customer Service](https://docs.microsoft.com/en-us/dynamics365/customer-service/help-hub) Live Chat Widget in a [WebView](https://developer.android.com/reference/android/webkit/WebView).

The sample app is not production ready code and are used for reference only.

## Prerequisites
- [Android](https://www.android.com/)

## Getting Started

### 1. Configure a chat widget

If you haven't set up a chat widget yet. Please follow these instructions on:

https://docs.microsoft.com/en-us/dynamics365/omnichannel/administrator/add-chat-widget

### 2. **Copy** the widget snippet code from the **Code snippet** section and save it somewhere. It will be needed later on.

It should look similar to this:

```html
<script
    id="Microsoft_Omnichannel_LCWidget"
    src="[your-src]"
    data-app-id="[your-app-id]"
    data-org-id="[your-org-id]"
    data-org-url="[your-org-url]"
>
</script>
```

### 3. **Add** your chat widget config to [OmnichannelConfig.kt](app/src/main/java/com/example/embedlcw/OmnichannelConfig.kt)

```kotlin
    val config = mapOf(
        "webPage" to "file:///android_asset/index.html",
        "src" to "[your-src]",
        "orgId" to "[your-org-id]",
        "orgUrl" to "[your-org-url]",
        "appId" to "[your-app-id]",
        "hideChatbutton" to "false",
        "renderMobile" to "false"
    )
```

## FAQ

### Q: Download attachment does not work

A: Additional native implementation is required to support attachment download.

Attachments sent/received are stored as `blob URL`. It is a known issue that WebViewClient can't load `blob URLs` in the Android community (reference [here](https://stackoverflow.com/questions/48892390/download-blob-file-from-website-inside-android-webviewclient/48954970#48954970)). A work around would be to convert the `blob URL` to a `Blob`, then to `Base64` data on the **web side**. The **native side** will decode the `Base64` data, write the data to a file based on the MIME type specified in the prefix of the `Base64` data.

It should be noted the domain where all `blob URLs` attachments and the `CDN URL` where the LCW is hosted are the same. It's important to ensure same-origin policy is enforced or it won't work for security reasons.

Steps to follow:

1. Create `JavascriptInterface` class with the following methods:

    a. Method to process `Base64` data and save it locally. Basically downloading the attachment in `Base64` format.

    ```kotlin
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
    ```

    b. Method to convert `blobUrl` to `Blob`, then process `Base64` data on native side

    ```kotlin
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
    ```

1. Add permissions in `AndroidManifest.xml`
    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    ```

1. Request permissions dynamically

    ```kotlin
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED);
    ```

1. Load WebView with `CDN URL` as `BaseURL`

    ```kotlin
    wv.loadDataWithBaseURL(baseUrl, data, "text/html", null, baseUrl)
    ```

1. Inject Kotlin object `JavascriptInterface` into WebView with `Android` as global variable in the DOM

    ```kotlin
    val javascriptInterface = JavascriptInterface(applicationContext)
    wv.addJavascriptInterface(javascriptInterface, "Android")
    ```

1. Subscribe to notification when content cannot be handled by WebView and should be handled in Native layer

    ```kotlin
    wv.setDownloadListener(DownloadListener { url, _, _, _, _ ->
        if (url.startsWith("blob:")) {
            wv.evaluateJavascript(javascriptInterface.getBase64StringFromBlobUrl(url), null)
        }
    })
    ```

### Q: Upload attachment does not work

A: Additional native implementation is required to support attachment download.

By default, WebView does not provide browser-like widgets. So, file-select field for file uploads is not natively supported. However, Android is exposing a method to handle the response when an user presses the "Select File" button: [WebChromeClient.onShowFileChooser()](https://developer.android.com/reference/android/webkit/WebChromeClient.html#onShowFileChooser(android.webkit.WebView,%20android.webkit.ValueCallback%3Candroid.net.Uri[]%3E,%20android.webkit.WebChromeClient.FileChooserParams))

Steps to follow:

1. Override `WebChromeClient`'s `onShowFileChooser()` method to launch a file picker which allows user to choose a file to upload

    ```kotlin
    wv.webChromeClient = object: WebChromeClient() {

        // Handles HTML forms with 'file' input type on android API 21+
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri?>?>,
            fileChooserParams: FileChooserParams
        ): Boolean {

            if (Build.VERSION.SDK_INT >= 21) {
                uploadedFileTempPath = filePathCallback // Track the uploaded file temporary path for later use
                val intent = fileChooserParams.createIntent() // Intent to start file picker
                try {
                    startActivityForResult(intent, YOUR_REQUEST_CODE) // Send any request code which represents your 'request' on exiting file picker activity
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
    ```

1. Override the `Activity`'s `onActivityResult()` method to catch the file the user selected, then send the data back to the WebView

    ```kotlin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Build.VERSION.SDK_INT >= 21) {

            if (resultCode == Activity.RESULT_CANCELED) {
                uploadedFileTempPath?.onReceiveValue(null) // Need to send null value to ensure future attempts workable
                return
            }

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == YOUR_REQUEST_CODE) {
                    uploadedFileTempPath?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data)) // Send result back to WebView
                    uploadedFileTempPath = null
                }
            }
        }
    }
    ```