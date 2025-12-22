package com.github.xingzheli.antibrainrot.utils

import android.content.Context
import java.util.Locale

object LocaleHelper {
    fun setLocale(context: Context, language: String): Context {
        val locale = Locale(language).apply { Locale.setDefault(this) }
        return updateConfiguration(context, locale)
    }

    private fun updateConfiguration(context: Context, locale: Locale): Context {
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}