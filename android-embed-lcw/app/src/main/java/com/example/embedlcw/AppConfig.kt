package com.example.embedlcw

class AppConfig {
    companion object {
        val config = mapOf(
            "showWebViewOnLcwReady" to false, // Wait for lcw:ready before loading the full application
            "useNativeChatButton" to false, // Use chat button to launch web view
        )
    }
}