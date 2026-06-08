package hr.algebra.voya.api

import android.content.Context

object TokenManager {

    private const val PREFS_NAME = "voya_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_FIRST_NAME = "user_first_name"
    private const val KEY_USER_LAST_NAME = "user_last_name"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_STATUS = "user_status"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(context: Context, token: String) {
        prefs(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        return prefs(context).getString(KEY_TOKEN, null)
    }

    fun saveUserId(context: Context, userId: Long) {
        prefs(context).edit().putLong(KEY_USER_ID, userId).apply()
    }

    fun saveUserProfile(
        context: Context,
        email: String,
        firstName: String,
        lastName: String,
        role: String,
        phone: String? = null,
        status: String? = null
    ) {
        prefs(context).edit()
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_FIRST_NAME, firstName)
            .putString(KEY_USER_LAST_NAME, lastName)
            .putString(KEY_USER_ROLE, role)
            .putString(KEY_USER_PHONE, phone)
            .putString(KEY_USER_STATUS, status)
            .apply()
    }

    fun getUserEmail(context: Context): String? = prefs(context).getString(KEY_USER_EMAIL, null)

    fun getUserFirstName(context: Context): String? = prefs(context).getString(KEY_USER_FIRST_NAME, null)

    fun getUserLastName(context: Context): String? = prefs(context).getString(KEY_USER_LAST_NAME, null)

    fun getUserPhone(context: Context): String? = prefs(context).getString(KEY_USER_PHONE, null)

    fun getUserRole(context: Context): String? = prefs(context).getString(KEY_USER_ROLE, null)

    fun getUserStatus(context: Context): String? = prefs(context).getString(KEY_USER_STATUS, null)

    fun getUserId(context: Context): Long? {
        val sharedPreferences = prefs(context)
        if (!sharedPreferences.contains(KEY_USER_ID)) {
            return null
        }

        val userId = sharedPreferences.getLong(KEY_USER_ID, -1L)
        return if (userId == -1L) null else userId
    }

    fun clear(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
