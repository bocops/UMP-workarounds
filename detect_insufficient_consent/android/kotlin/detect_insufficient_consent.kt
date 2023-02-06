/**
 * Possible return values of [detectAdConfiguration]
 */
enum class AdConfiguration {
    /**
     * App should be able to show personalized ads for all configured vendors.
     */
    ALL,

    /**
     * App should be able to show at most non-personalized ads as defined by Google.
     */
    NONPERSONALIZED,

    /**
     * App should be able to show at most limited ads as defined by Google.
     */
    LIMITED,

    /**
     * Display of any ads is unclear, mostly due to a problem with figuring out which vendors were properly configured.
     */
    UNCLEAR,

    /**
     * No ads will be shown due to lacking consent or legitimate interest.
     */
    NONE
}

/**
 * Checks the stored IABTCF configuration and returns one of the values defined in [AdConfiguration],
 * based on the necessary minimum consent/interest defined here: https://support.google.com/admob/answer/9760862
 */
private fun detectAdConfiguration(context: Context) : AdConfiguration {
    // default string for "no consent", used in cases where no configuration has previously been stored
    val defaultPurposeString = "0000000000"

    // IABTCF strings are stored in SharedPreferences
    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    // relevant strings are those for purpose consent or legitimate interest, as well as vendors
    val tcConsentString = sharedPrefs
        .getString("IABTCF_PurposeConsents", defaultPurposeString) ?: defaultPurposeString
    val tcInterestString = sharedPrefs
        .getString("IABTCF_PurposeLegitimateInterests", defaultPurposeString) ?: defaultPurposeString
    val tcVendorString = sharedPrefs
        .getString("IABTCF_VendorConsents", "0") ?: "0"

    // we need consent for the following purposes N, stored in positions N-1 of the consent string:
    //   1, 3 and 4 to show all ads
    //   1 to show non-personalized ads
    //   no consent to show limited ads
    val maxAdDisplayConfiguration = when {
        (tcConsentString[0] == '1' && tcConsentString[2] == '1' && tcConsentString[3] == '1') -> AdConfiguration.ALL
        (tcConsentString[0] == '1') -> AdConfiguration.NONPERSONALIZED
        else -> AdConfiguration.LIMITED
    }

    // in any case we need at least legitimate interest for purposes N = 2, 7, 9 and 10,
    // stored in positions N-1 of either purpose string:
    val sufficientInterest = (
            (tcConsentString[1] == '1' || tcInterestString[1] == '1') &&
                    (tcConsentString[1] == '6' || tcInterestString[1] == '6') &&
                    (tcConsentString[1] == '8' || tcInterestString[1] == '8') &&
                    (tcConsentString[1] == '9' || tcInterestString[1] == '9')
            )
    if (!sufficientInterest) {
        return AdConfiguration.NONE
    }

    // TODO vendor configuration is variable, so needs to be defined by the individual developer
    //   - run app and make sure that a valid configuration is stored
    //   - have the app log the value of [tcVendorString], then copy that value to the following line
    //   - repeat if ad configuration changes, perhaps make this value available via remote configuration instead
    val goodVendorConfiguration = "TODO"

    // if the stored string is shorter than what is necessary, at least some vendors will not be
    // configured properly.
    if (tcVendorString.length < goodVendorConfiguration.length) {
        return AdConfiguration.UNCLEAR
    }

    // build a regex that must match all '1' but not the '0' characters in goodVendorConfiguration,
    // and allows this configuration to be shorter than the string it is compared with
    val vendorRegex = Regex(goodVendorConfiguration.replace("0",".").plus(".*"))

    //if the regex matches, at least some ads should be served; if not, vendor string is unclear
    return if (vendorRegex.matches(tcVendorString)) {
        maxAdDisplayConfiguration
    } else {
        return AdConfiguration.UNCLEAR
    }
}