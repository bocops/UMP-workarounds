/**
 * This function checks the date of last consent, which is base64-encoded in digits 1..7 of a string that is stored
 * in SharedPreferences under the key "IABTCF_TCString".
 *
 * If this date is older than 365 days, the entry with that key will be removed from SharedPreferences. With the IABTCF
 * configuration now being invalid, the CMP should re-display the consent dialog the next time it is instantiated.
 *
 * This should avoid errors of any used ad solution, which is supposed to consider consent older than 13 months "outdated".
 */
public void deleteTCStringIfOutdated(Context context) {
    // IABTCF string is stored in SharedPreferences
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

    // get IABTCF string containing creation timestamp;
    // if the key does not exist, there is no IABTCF string to check; return early
    String tcString = sharedPrefs.getString("IABTCF_TCString", null);
    if (tcString == null) {
        return;
    }

    // base64 alphabet used to store data in IABTCF string
    String base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

    // date is stored in digits 1..7 of the IABTCF string
    String dateSubstring = tcString.substring(1,7);

    // interpret date substring as base64-encoded integer value
    long timestamp = 0;
    for (int i = 0; i < dateSubstring.length(); i++) {
        char c = dateSubstring.charAt(i);

        int value = base64.indexOf(c);
        timestamp = timestamp * 64 + value;
    }

    // timestamp is given is deci-seconds, convert to milliseconds
    timestamp *= 100;

    // compare with current timestamp to get age in days
    long daysAgo = (System.currentTimeMillis() - timestamp) / (1000*60*60*24);

    // delete TC string if age is over a year
    if (daysAgo > 365) {
        sharedPrefs.edit().remove("IABTCF_TCString").apply();
    }
}
