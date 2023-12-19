/**
 * Checks the SharedPreferences file created by Google UMP, and returns the int value of the cached consent status
 * according to https://developers.google.com/admob/android/privacy/api/reference/com/google/android/ump/ConsentInformation.ConsentStatus
 * These should be as follow:
 * 0 - UNKNOWN
 * 1 - NOT_REQUIRED
 * 2 - REQUIRED
 * 3 - OBTAINED
 */
private fun getPreviousConsentStatus(context: Context) {
    val sp = context.getSharedPreferences("__GOOGLE_FUNDING_CHOICE_SDK_INTERNAL__", Context.MODE_PRIVATE)
    return sp.getInt("consent_status", 0)
}