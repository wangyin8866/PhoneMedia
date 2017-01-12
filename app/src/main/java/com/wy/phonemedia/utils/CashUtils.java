package com.wy.phonemedia.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.wy.phonemedia.service.MusicPlayerService;

/**
 * Created by Wyman on 2016/9/29.
 * WeChat: wy391920778
 * Effect:缓存
 */

public class CashUtils {
    /**
     * 存储网络json数据
     * @param context
     * @param key
     * @param values
     */
    public static void putString(Context context, String key, String values) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("wy", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, values).apply();

    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("wy", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    /**
     * 存储播放模式
     * @param context
     * @param key
     * @param values
     */
    public static void putPlayMode(Context context, String key, int values) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("wy", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, values).apply();

    }
    public static int getPlayMode(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("wy", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, MusicPlayerService.PLAY_MODE_NORMAL);
    }
}
