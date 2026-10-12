package com.example.pocketflow

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import org.json.JSONArray
import org.json.JSONObject

private const val PREFS = "pocketflow"
private const val ENTRIES = "entries"

data class Entry(
    val id: String,
    val amount: Double,
    val title: String,
    val category: String,
    val date: Long,
    val kind: String,
    val status: String = "confirmed"
) {
    fun json() = JSONObject()
        .put("id", id)
        .put("amount", amount)
        .put("title", title)
        .put("category", category)
        .put("date", date)
        .put("kind", kind)
        .put("status", status)
}

object Store {
    fun add(context: android.content.Context, entry: Entry) {
        val prefs = context.getSharedPreferences(PREFS, 0)
        val entries = read(prefs.getString(ENTRIES, "[]"))
        if ((0 until entries.length()).any { entries.optJSONObject(it)?.optString("id") == entry.id }) return
        entries.put(entry.json())
        while (entries.length() > 500) entries.remove(0)
        prefs.edit().putString(ENTRIES, entries.toString()).apply()
    }

    fun list(context: android.content.Context): List<Entry> {
        val entries = read(context.getSharedPreferences(PREFS, 0).getString(ENTRIES, "[]"))
        return (0 until entries.length()).mapNotNull { index ->
            try {
                val item = entries.getJSONObject(index)
                Entry(
                    id = item.getString("id"),
                    amount = item.getDouble("amount"),
                    title = item.getString("title"),
                    category = item.getString("category"),
                    date = item.getLong("date"),
                    kind = item.getString("kind"),
                    status = item.optString("status", "review")
                )
            } catch (_: Exception) {
                null
            }
        }.sortedByDescending { it.date }
    }

    fun captured(context: android.content.Context): JSONArray = JSONArray().apply {
        list(context).filter { it.kind == "detected" }.forEach { put(it.json()) }
    }

    fun acknowledgeCaptured(context: android.content.Context, idsJson: String) {
        val ids = try {
            val array = JSONArray(idsJson)
            (0 until array.length()).mapNotNull { array.optString(it).takeIf(String::isNotBlank) }.toSet()
        } catch (_: Exception) {
            return
        }
        if (ids.isEmpty()) return
        val prefs = context.getSharedPreferences(PREFS, 0)
        val old = read(prefs.getString(ENTRIES, "[]"))
        val kept = JSONArray()
        for (index in 0 until old.length()) {
            val item = old.optJSONObject(index) ?: continue
            val shouldRemove = item.optString("kind") == "detected" && item.optString("id") in ids
            if (!shouldRemove) kept.put(item)
        }
        prefs.edit().putString(ENTRIES, kept.toString()).apply()
    }

    fun clearCaptured(context: android.content.Context) {
        val prefs = context.getSharedPreferences(PREFS, 0)
        val old = read(prefs.getString(ENTRIES, "[]"))
        val kept = JSONArray()
        for (index in 0 until old.length()) {
            val item = old.optJSONObject(index) ?: continue
            if (item.optString("kind") != "detected") kept.put(item)
        }
        prefs.edit().putString(ENTRIES, kept.toString()).apply()
    }

    private fun read(raw: String?): JSONArray = try {
        JSONArray(raw ?: "[]")
    } catch (_: Exception) {
        JSONArray()
    }
}

class MainActivity : Activity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false
        webView.addJavascriptInterface(PocketFlowBridge(this), "PocketFlowNative")

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            override fun onPageFinished(view: WebView, url: String) {
                view.evaluateJavascript("window.pocketFlowRefresh && window.pocketFlowRefresh()", null)
            }
        }
        setContentView(webView)
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")
    }

    override fun onResume() {
        super.onResume()
        if (::webView.isInitialized) {
            webView.post { webView.evaluateJavascript("window.pocketFlowRefresh && window.pocketFlowRefresh()", null) }
        }
    }

    @Deprecated("Deprecated in Android API, retained for compatibility")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}

class PocketFlowBridge(private val activity: Activity) {
    @JavascriptInterface
    fun getCapturedExpenses(): String = Store.captured(activity).toString()

    @JavascriptInterface
    fun acknowledgeCapturedExpenses(idsJson: String) = Store.acknowledgeCaptured(activity, idsJson)

    @JavascriptInterface
    fun clearCapturedExpenses() = Store.clearCaptured(activity)

    @JavascriptInterface
    fun hasNotificationAccess(): Boolean {
        val enabled = Settings.Secure.getString(activity.contentResolver, "enabled_notification_listeners").orEmpty()
        val component = ComponentName(activity, TransactionNotificationListener::class.java).flattenToString()
        return enabled.split(':').contains(component)
    }

    @JavascriptInterface
    fun openNotificationAccessSettings() {
        activity.runOnUiThread {
            activity.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
    }
}
