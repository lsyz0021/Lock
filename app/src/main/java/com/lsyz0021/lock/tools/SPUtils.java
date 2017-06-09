package com.lsyz0021.lock.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.lsyz0021.lock.App;

/**
 * 作者：lcw 16-8-30
 * 博客：http://blog.csdn.net/lsyz0021/
 * SharedPreferences 工具类
 */
public class SPUtils {

    private static SharedPreferences sp;

    private static SharedPreferences getSharedPreference() {
        if (sp == null) {
            sp = App.getContext().getSharedPreferences("userDefault", Context.MODE_PRIVATE);
        }
        return sp;
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences sp = getSharedPreference();
        sp.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sp = getSharedPreference();
        return sp.getBoolean(key, defValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences sp = getSharedPreference();
        sp.edit().putString(key, value).apply();
    }

    public static String getString(String key, String defValue) {
        SharedPreferences sp = getSharedPreference();
        return sp.getString(key, defValue);
    }

    public static void putInt(String key, int value) {
        SharedPreferences sp = getSharedPreference();
        sp.edit().putInt(key, value).apply();
    }

    public static int getInt(String key, int defValue) {
        SharedPreferences sp = getSharedPreference();
        return sp.getInt(key, defValue);
    }

    public static void remove(String key) {
        SharedPreferences sp = getSharedPreference();
        sp.edit().remove(key).apply();
    }
}
