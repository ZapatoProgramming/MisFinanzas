import android.content.Context
import androidx.core.content.edit

object PreferencesManager {

    private const val PREFS_NAME = "misfinanzas_prefs"
    private const val KEY_NOTIFICATIONS_GRANTED = "notifications_granted"

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit() { putBoolean(KEY_NOTIFICATIONS_GRANTED, enabled) }
    }

    fun areNotificationsEnabled(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(KEY_NOTIFICATIONS_GRANTED, false)
    }
}