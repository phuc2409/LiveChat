package com.livechat.helper

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User: Quang Phúc
 * Date: 2023-04-16
 * Time: 10:02 AM
 */
@Singleton
class SharedPreferencesHelper @Inject constructor(context: Context) {

    private object Key {
        const val TOKEN = "TOKEN"
    }

    private val prefsName = "com_livechat_prefs"
    private val prefs = context.getSharedPreferences(prefsName, 0)
    private val editor = prefs.edit()

    private fun sharedPreferenceExist(key: String?): Boolean {
        return prefs.contains(key)
    }

    private fun getString(key: String, default: String = ""): String {
        return if (sharedPreferenceExist(key)) {
            prefs.getString(key, default) ?: default
        } else {
            default
        }
    }

    private fun setString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    private fun getBoolean(key: String, default: Boolean = false): Boolean {
        return if (sharedPreferenceExist(key)) {
            prefs.getBoolean(key, default)
        } else {
            default
        }
    }

    private fun setBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    private fun getInt(key: String, default: Int = 0): Int {
        return if (sharedPreferenceExist(key)) {
            prefs.getInt(key, default)
        } else {
            default
        }
    }

    private fun setInt(key: String, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    fun clearAll() {
        editor.clear()
        editor.apply()
    }

    fun getToken(): String {
        return getString(Key.TOKEN, "")
    }

    fun setToken(value: String) {
        setString(Key.TOKEN, value)
    }
}