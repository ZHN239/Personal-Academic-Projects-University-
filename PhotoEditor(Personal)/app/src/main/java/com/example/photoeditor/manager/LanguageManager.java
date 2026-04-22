package com.example.photoeditor.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.res.Resources;
import java.util.Locale;

public class LanguageManager {
    private static final String KEY = "app_language";

    // Save and apply language
    public static void setLanguage(Context ctx, String lang) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        sp.edit().putString(KEY, lang).apply();

        Locale locale = "zh-rTW".equals(lang)
                ? Locale.TRADITIONAL_CHINESE
                : new Locale(lang);

        Locale.setDefault(locale);
        Resources res = ctx.getResources();
        res.getConfiguration().setLocale(locale);
        res.updateConfiguration(res.getConfiguration(), res.getDisplayMetrics());
    }

    // Get saved language
    public static String getLanguage(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String saved = sp.getString(KEY, null);
        return saved != null ? saved : Locale.getDefault().getLanguage();
    }
}