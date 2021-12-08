package com.me.nav.vvo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout

const val PREFS_NAME = "theme_prefs"
const val KEY_THEME = "prefs.theme"
const val THEME_LIGHT = 0
const val THEME_DARK = 1
class AboutActivity : AppCompatActivity() {
    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val backBtn = findViewById<ImageButton>(R.id.btn_about_back)
        val themeView = findViewById<ConstraintLayout>(R.id.themeView)
        val underlineThemeView = findViewById<LinearLayout>(R.id.underThemeView)
        val switch = findViewById<Switch>(R.id.dark_mode_switch)
        val rateUsBtn = findViewById<Button>(R.id.rate_us)

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

        rateUsBtn.setOnClickListener {
            val uri: Uri = Uri.parse("market://details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
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

}