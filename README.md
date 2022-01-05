# Omnichannel Embed LCW

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

### 3. **Add** your chat widget config to [OmnichannelConfig.kt](app\src\main\java\com\example\embedlcw\OmnichannelConfig.kt)

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