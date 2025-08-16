package com.example.firebasechatapp.data.local

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {

    companion object {
        private const val PREF_NAME = "app_preference"

        private const val CURRENT_USER_ID = "current_user_id"
        private const val USER_LOGIN_STATUS = "user_login_status"

    }

    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()

    fun getString(key: String): String? = pref.getString(key, null)
    fun setString(key: String, value: String?) = editor.putString(key, value).apply()

    fun getBoolean(key: String): Boolean = pref.getBoolean(key, false)
    fun setBoolean(key: String, value: Boolean) = editor.putBoolean(key, value).apply()

    fun clearAll() = editor.clear().apply()

    fun isUserLoggedIn() = getBoolean(USER_LOGIN_STATUS)

    fun setUserLoggedIn(loggedIn: Boolean) = setBoolean(USER_LOGIN_STATUS, loggedIn)
    fun saveUser(uid: String) {
        setString(CURRENT_USER_ID, uid)
    }
}
