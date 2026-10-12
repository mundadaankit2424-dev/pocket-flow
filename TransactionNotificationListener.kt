package com.example.pocketflow

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.util.Locale
import java.util.UUID

class TransactionNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == packageName) return
        val extras = sbn.notification.extras ?: return
        val title = extras.getCharSequence("android.title")?.toString().orEmpty()
        val bodyParts = mutableListOf<String>()
        extras.getCharSequence("android.text")?.toString()?.let(bodyParts::add)
        extras.getCharSequence("android.bigText")?.toString()?.let(bodyParts::add)
        extras.getCharSequenceArray("android.textLines")?.forEach { bodyParts.add(it.toString()) }
        val body = bodyParts.distinct().joinToString(" ")
        val raw = "$title $body".trim()
        if (raw.isBlank()) return

        val text = raw.lowercase(Locale.ROOT)
        // Never save one-time codes, login credentials, or alerts that are not completed debits.
        val excluded = listOf(
            "otp", "one time password", "one-time password", "verification code",
            "login code", "upi pin", "password", "credited", "credit received",
            "refund", "cashback", "reversal", "reversed", "failed", "declined"
        )
        if (excluded.any(text::contains)) return
        val debitSignals = listOf(
            "debited", "debit", "spent", "paid", "purchase", "withdrawn",
            "withdrawal", "transaction", "payment successful", "sent rs", "sent inr"
        )
        if (debitSignals.none(text::contains)) return

        val amountMatch = Regex(
            "(?:₹\\s*|Rs\\.?\\s*|INR\\s*)([0-9][0-9,]*(?:\\.[0-9]{1,2})?)",
            RegexOption.IGNORE_CASE
        ).find(raw) ?: return
        val amount = amountMatch.groupValues[1].replace(",", "").toDoubleOrNull() ?: return
        if (amount <= 0) return

        val merchant = body
            .replace(Regex("(?i)(debited|debit|spent|paid|purchase|withdrawn|withdrawal|transaction|payment successful|from|at|on|₹|rs\\.?|inr|[0-9][0-9,]*(?:\\.[0-9]{1,2})?)"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(48)
            .ifBlank { title.take(48).ifBlank { "Bank transaction" } }
        val category = when {
            listOf("tea", "cafe", "coffee", "restaurant", "food", "swiggy", "zomato").any(text::contains) -> "Food & drinks"
            listOf("uber", "ola", "metro", "fuel", "petrol", "cab").any(text::contains) -> "Travel"
            listOf("bill", "electricity", "recharge", "broadband").any(text::contains) -> "Bills"
            listOf("amazon", "flipkart", "shopping").any(text::contains) -> "Shopping"
            else -> "Other"
        }

        // Keep only a bounded set of notification IDs for duplicate suppression. Raw messages are not saved.
        val prefs = getSharedPreferences("pocketflow", MODE_PRIVATE)
        val seen = prefs.getString("seen", "").orEmpty().split("\n").filter(String::isNotBlank).toMutableSet()
        val key = "${sbn.packageName}:${sbn.key}"
        if (key in seen) return
        seen.add(key)
        prefs.edit().putString("seen", seen.toList().takeLast(200).joinToString("\n")).apply()

        Store.add(
            this,
            Entry(UUID.randomUUID().toString(), amount, merchant, category, System.currentTimeMillis(), "detected", "review")
        )
    }
}
