package com.example.pocketflow

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val PREFS = "pocketflow"
private const val ENTRIES = "entries"
private const val SALARY = "salary"
private val CATEGORIES = arrayOf("Food & drinks", "Travel", "Bills", "Shopping", "Other")

data class Entry(val id: String, val amount: Double, val title: String, val category: String, val date: Long, val kind: String, val status: String = "confirmed") {
    fun json() = JSONObject().put("id", id).put("amount", amount).put("title", title).put("category", category).put("date", date).put("kind", kind).put("status", status)
}
object Store {
    fun add(c: android.content.Context, e: Entry) { val p=c.getSharedPreferences(PREFS,0); val a=read(p.getString(ENTRIES,"[]")); a.put(e.json()); p.edit().putString(ENTRIES,a.toString()).apply() }
    fun list(c: android.content.Context): List<Entry> { val a=read(c.getSharedPreferences(PREFS,0).getString(ENTRIES,"[]")); return (0 until a.length()).mapNotNull { i -> try { val o=a.getJSONObject(i); Entry(o.optString("id",i.toString()),o.getDouble("amount"),o.getString("title"),o.getString("category"),o.getLong("date"),o.getString("kind"),o.optString("status",if(o.getString("kind")=="detected") "review" else "confirmed")) } catch (_:Exception){null} }.sortedByDescending{it.date} }
    fun update(c:android.content.Context,e:Entry) { replace(c,e.id,e.json()) }
    fun delete(c:android.content.Context,id:String) { val p=c.getSharedPreferences(PREFS,0); val old=read(p.getString(ENTRIES,"[]")); val a=JSONArray(); for(i in 0 until old.length()){val o=old.getJSONObject(i);if(o.optString("id")!=id)a.put(o)};p.edit().putString(ENTRIES,a.toString()).apply() }
    private fun replace(c:android.content.Context,id:String,j:JSONObject) { val p=c.getSharedPreferences(PREFS,0); val old=read(p.getString(ENTRIES,"[]")); val a=JSONArray(); for(i in 0 until old.length()){val o=old.getJSONObject(i);a.put(if(o.optString("id")==id)j else o)};p.edit().putString(ENTRIES,a.toString()).apply() }
    private fun read(s:String?)=try{JSONArray(s?:"[]")}catch(_:Exception){JSONArray()}
}

class MainActivity : Activity() {
    private lateinit var root:LinearLayout
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);render()}
    override fun onResume(){super.onResume();if(::root.isInitialized)render()}
    private fun render(){
        root=LinearLayout(this).apply{orientation=1;setPadding(24,22,24,24);setBackgroundColor(0xFFF5F7F4.toInt())}
        val scroll=ScrollView(this);scroll.addView(root);setContentView(scroll)
        root.addView(label("PocketFlow",29,true));root.addView(label("Your money tracker · private and offline",14,false))
        val p=getSharedPreferences(PREFS,0);val salary=EditText(this).apply{hint="Monthly salary (₹)";inputType=2;setText(p.getString(SALARY,""))};root.addView(salary,lp())
        root.addView(button("Save salary"){val n=salary.text.toString().toDoubleOrNull();if(n!=null)p.edit().putString(SALARY,n.toString()).apply() else toast("Enter a valid salary");render()})
        val fixedName=EditText(this).apply{hint="Monthly fixed cost (e.g. Rent)"};val fixedAmount=EditText(this).apply{hint="Amount (₹)";inputType=2}
        root.addView(label("Monthly fixed costs",20,true));root.addView(fixedName,lp());root.addView(fixedAmount,lp())
        root.addView(button("Add monthly cost"){val n=fixedAmount.text.toString().toDoubleOrNull();if(n!=null&&fixedName.text.isNotBlank()){Store.add(this,Entry(java.util.UUID.randomUUID().toString(),n,fixedName.text.toString(),"Bills",System.currentTimeMillis(),"fixed"));render()}else toast("Enter a name and amount")})
        root.addView(label("Add a daily expense",20,true));val expName=EditText(this).apply{hint="What did you spend on?"};val expAmount=EditText(this).apply{hint="Amount (₹)";inputType=2};val expCat=spinner(CATEGORIES)
        root.addView(expName,lp());root.addView(expAmount,lp());root.addView(expCat,lp())
        root.addView(button("Add expense"){val n=expAmount.text.toString().toDoubleOrNull();if(n!=null&&expName.text.isNotBlank()){Store.add(this,Entry(java.util.UUID.randomUUID().toString(),n,expName.text.toString(),expCat.selectedItem.toString(),System.currentTimeMillis(),"expense"));render()}else toast("Enter a description and amount")})
        root.addView(label("Automatic transaction alerts",20,true));root.addView(label("When enabled, matching transaction notifications are saved for your review. Android gives notification access broadly; details stay on this device.",14,false))
        root.addView(button("Open notification access settings"){startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))})
        val all=Store.list(this);val cal=Calendar.getInstance();val m=cal.get(Calendar.MONTH);val y=cal.get(Calendar.YEAR)
        val current=all.filter{it.kind=="fixed" || (Calendar.getInstance().apply{timeInMillis=it.date}.let{d->d.get(Calendar.MONTH)==m&&d.get(Calendar.YEAR)==y})}
        val fixed=current.filter{it.kind=="fixed"}.sumOf{it.amount};val spent=current.filter{it.kind!="fixed"&&it.status=="confirmed"}.sumOf{it.amount};val income=p.getString(SALARY,"0")?.toDoubleOrNull()?:0.0
        root.addView(label("This month",20,true));root.addView(label("Salary  ₹${money(income)}\nFixed costs  −₹${money(fixed)}\nConfirmed spending  −₹${money(spent)}\nBalance after listed items  ₹${money(income-fixed-spent)}",16,false))
        root.addView(label("Needs your review",20,true));val pending=all.filter{it.status=="review"}
        if(pending.isEmpty())root.addView(label("No unreviewed transactions.",14,false))
        pending.forEach{e->val card=LinearLayout(this).apply{orientation=1;setPadding(12,8,12,8)};card.addView(label("₹${money(e.amount)} · ${e.title}",16,true));val sp=spinner(CATEGORIES);sp.setSelection(CATEGORIES.indexOf(e.category).coerceAtLeast(0));card.addView(sp);card.addView(button("Confirm expense"){Store.update(this,e.copy(category=sp.selectedItem.toString(),status="confirmed"));render()});card.addView(button("Ignore this alert"){Store.delete(this,e.id);render()});root.addView(card)}
        root.addView(label("Recent activity",20,true));if(all.isEmpty())root.addView(label("Add your first entry above.",14,false))
        all.filter{it.status!="review"}.take(50).forEach{e->val line=LinearLayout(this).apply{orientation=0;gravity=android.view.Gravity.CENTER_VERTICAL};val display=if(e.kind=="fixed")"Monthly" else e.category;line.addView(label("${if(e.kind=="fixed")"₹" else "−₹"}${money(e.amount)}  ${e.title}\n$display · ${SimpleDateFormat("d MMM",Locale.getDefault()).format(Date(e.date))}",15,false),LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f));line.addView(button("Remove"){Store.delete(this,e.id);render()});root.addView(line)}
    }
    private fun spinner(items:Array<String>)=Spinner(this).apply{adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,items)}
    private fun label(s:String,z:Int,b:Boolean)=TextView(this).apply{text=s;textSize=z.toFloat();setTextColor(0xFF1D2922.toInt());if(b)setTypeface(null,1);setPadding(0,10,0,6)}
    private fun lp()=LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun button(s:String,f:()->Unit)=Button(this).apply{text=s;setOnClickListener{f()}}
    private fun money(x:Double)=String.format(Locale.getDefault(),"%,.0f",x)
    private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
}
