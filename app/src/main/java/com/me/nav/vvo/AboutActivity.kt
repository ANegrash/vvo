package com.me.nav.vvo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.*

const val PREFS_NAME = "theme_prefs"
const val KEY_THEME = "prefs.theme"
const val KEY_LANG = "prefs.lang"
const val THEME_LIGHT = 0
const val THEME_DARK = 1

class AboutActivity : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    private var langsArray = arrayOf("Русский", "English")
    private var langCodesArray = arrayOf("ru", "en")
    private var langTitlesArray = arrayOf("Перазпуск приложения", "Restart the app")
    private var langMessagesArray = arrayOf("Для установки русского языка требуется перезапуск приложения", "The application will be restarted to install the English language")

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val backBtn = findViewById<ImageButton>(R.id.btn_about_back)
        val themeView = findViewById<ConstraintLayout>(R.id.themeView)
        val underlineThemeView = findViewById<LinearLayout>(R.id.underThemeView)
        val switch = findViewById<Switch>(R.id.dark_mode_switch)
        val spinnerLanguage = findViewById<Spinner>(R.id.spinnerLanguage)
        val rateUsBtn = findViewById<Button>(R.id.rate_us)
        val phoneTv = findViewById<TextView>(R.id.phoneTv)
        val mapTv = findViewById<TextView>(R.id.mapTv)
        val devEmailTv = findViewById<TextView>(R.id.devEmailTv)
        val devLogo = findViewById<ImageView>(R.id.logoImg)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            themeView.visibility = View.VISIBLE
            underlineThemeView.visibility = View.VISIBLE
            switch.isChecked = getSavedTheme() == THEME_DARK
            switch.setOnCheckedChangeListener{ _, isChecked ->
                if (isChecked) {
                    setTheme(AppCompatDelegate.MODE_NIGHT_YES, THEME_DARK)
                } else {
                    setTheme(AppCompatDelegate.MODE_NIGHT_NO, THEME_LIGHT)
                }
            }
        } else {
            themeView.visibility = View.GONE
            underlineThemeView.visibility = View.GONE
        }

        val adapterLangs = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, langsArray)
        adapterLangs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = adapterLangs
        val currentLang = getSavedLang()
        var selectedLang = 0
        if (currentLang == "ru") {
            selectedLang = 0
        } else if (currentLang == "en") {
            selectedLang = 1
        }
        spinnerLanguage.setSelection(selectedLang, false)

        val itemSelectedListenerLangs: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected (
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                saveLang(langCodesArray[position])
                val builder = AlertDialog.Builder(this@AboutActivity)
                builder.setTitle(langTitlesArray[position])
                builder.setMessage(langMessagesArray[position])
                builder.setPositiveButton("OK"){ _, _ ->
                    val restartedIntent = baseContext.packageManager
                        .getLaunchIntentForPackage(baseContext.packageName)
                    restartedIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(restartedIntent)
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerLanguage.onItemSelectedListener = itemSelectedListenerLangs

        rateUsBtn.setOnClickListener {
            val uri: Uri = Uri.parse("market://details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
            }
        }

        phoneTv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW);
            intent.data = Uri.parse("tel:"+getString(R.string.phone))
            startActivity(intent)
        }

        mapTv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW);
            intent.data = Uri.parse("geo:0,0?q="+getString(R.string.address))
            startActivity(intent)
        }

        devEmailTv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW);
            intent.data = Uri.parse("mailto:"+getString(R.string.dev_email))
            startActivity(intent)
        }

        devLogo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW);
            intent.data = Uri.parse("https://nav-com.ru")
            startActivity(intent)
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setTheme(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        saveTheme(prefsMode)
    }

    private fun saveTheme(theme: Int) = sharedPrefs.edit().putInt(KEY_THEME, theme).apply()

    private fun getSavedTheme() = sharedPrefs.getInt(KEY_THEME, 0)

    private fun saveLang(lang: String) = sharedPrefs.edit().putString(KEY_LANG, lang).apply()

    private fun getSavedLang() = sharedPrefs.getString(KEY_LANG, "ru")

}