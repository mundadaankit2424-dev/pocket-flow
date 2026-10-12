package com.example.pocketflow

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.util.Locale
import java.util.UUID

class TransactionNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val ex=sbn.notification.extras?:return
        val title=ex.getCharSequence("android.title")?.toString().orEmpty()
        val body=listOfNotNull(ex.getCharSequence("android.text")?.toString(),ex.getCharSequence("android.bigText")?.toString()).joinToString(" ")
        val raw="$title $body".trim();if(raw.isBlank())return
        val t=raw.lowercase(Locale.ROOT)
        if(listOf("otp","one time password","verification code","login code","upi pin","password").any{t.contains(it)})return
        if(!listOf("debited","spent","paid","purchase","withdrawn","transaction").any{t.contains(it)})return
        val match=Regex("(?:₹|rs\\.?\\s?|inr\\s?)([0-9,]+(?:\\.[0-9]{1,2})?)",RegexOption.IGNORE_CASE).find(raw)?:return
        val amount=match.groupValues[1].replace(",","").toDoubleOrNull()?:return
        val merchant=body.replace(Regex("(?i)(debited|spent|paid|purchase|withdrawn|transaction|from|at|on|₹|rs\\.?|inr|[0-9,]+(?:\\.[0-9]{1,2})?)")," ").replace(Regex("\\s+")," ").trim().take(48).ifBlank{"Transaction alert"}
        val category=when{listOf("tea","cafe","coffee","restaurant","food","swiggy","zomato").any{t.contains(it)}->"Food & drinks";listOf("uber","ola","metro","fuel","petrol","cab").any{t.contains(it)}->"Travel";listOf("bill","electricity","recharge","broadband").any{t.contains(it)}->"Bills";listOf("amazon","flipkart","shopping").any{t.contains(it)}->"Shopping";else->"Other"}
        // Listener reposts can occur; ignore the same source notification key if already captured.
        val seen=getSharedPreferences("pocketflow",0).getString("seen","").orEmpty().split("\n").toMutableSet()
        val key="${sbn.packageName}:${sbn.key}"
        if(key in seen)return
        seen.add(key);getSharedPreferences("pocketflow",0).edit().putString("seen",seen.takeLast(200).joinToString("\n")).apply()
        Store.add(this,Entry(UUID.randomUUID().toString(),amount,merchant,category,System.currentTimeMillis(),"detected","review"))
    }
}
