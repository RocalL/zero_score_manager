package com.zerogame.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {
    private const val PREF_NAME = "locale_prefs"
    private const val KEY_LOCALE = "app_locale"

    private val locales = mapOf(
        "fr" to Locale("fr"),
        "en" to Locale("en")
    )

    fun getSavedLocale(context: Context): String {
        val prefs = getPrefs(context)
        return prefs.getString(KEY_LOCALE, "fr") ?: "fr"
    }

    fun setLocale(context: Context, localeCode: String) {
        getPrefs(context).edit().putString(KEY_LOCALE, localeCode).apply()
        applyLocale(localeCode)
    }

    fun applyLocale(localeCode: String) {
        val locale = locales[localeCode] ?: Locale("fr")
        val appLocale = LocaleListCompat.create(locale)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun getCurrentLocaleCode(context: Context): String {
        return getSavedLocale(context)
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
}
