package com.me.nav.vvo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.util.*

class SplashActivity : AppCompatActivity() {
    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Language change
        val config = resources.configuration
        val lang = getSavedLang()
        val locale = Locale(lang)
        Locale.setDefault(locale)
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)

        //Theme change
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (getSavedTheme() == THEME_DARK)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getSavedTheme() = sharedPrefs.getInt(KEY_THEME, 0)

    private fun getSavedLang() = sharedPrefs.getString(KEY_LANG, "ru")
}