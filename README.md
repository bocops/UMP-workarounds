# UMP-workarounds
This repository contains workarounds for shortcomings in Google's "User Messaging Platform" (UMP) SDK.

## What is Google UMP?
Google UMP is a commercial "Consent Management Platform" (CMP) as defined by the "IAB Europe Transparency & Consent Framework":

> “Transparency and Consent Management Platform” (“Consent Management Platform”, or “CMP”) means the company or
> organisation that centralises and manages transparency for, and consent and objections of the end user.
> The CMP can read and update the Legal Basis status of Vendors on the GVL, and acts as an intermediary between
> a Publisher, an end user, and Vendors to provide transparency, help Vendors and Publishers establish Legal Bases
> for processing, acquire user consent as needed and manage user objections, and communicate Legal Basis, consent or
> and/or objection status to the ecosystem. A CMP may be the party that surfaces, usually on behalf of the publisher,
> the UI to a user, though that may also be another party. CMPs may be private or commercial. A private CMP means
> a Publisher that implements its own CMP for its own purposes. A commercial CMP offers CMP services to other parties.
> Unless specifically noted otherwise, these policies apply to both private and commercial CMPs.
>
> _(copied from: https://iabeurope.eu/iab-europe-transparency-consent-framework-policies/#_Chapter_I_Definitions on 2023-01-18)_

Google claims that UMP is a generally useful product for developers that need to ensure compatibility
of _their_ software with the General Data Protection Regulation (GDPR):

> Under the Google EU User Consent Policy, you must make certain disclosures to your users in the
> European Economic Area (EEA) along with the UK and obtain their consent to use cookies or other local storage,
> where legally required, and to use personal data (such as AdID) to serve ads. This policy reflects the requirements
> of the EU ePrivacy Directive and the General Data Protection Regulation (GDPR).
>
> To support publishers in meeting their duties under this policy, Google offers the User Messaging Platform (UMP) SDK,
> which replaces the previous open source Consent SDK. The UMP SDK has been updated to support the latest IAB standards.
> 
> _(copied from: https://developers.google.com/admob/ump/android/quick-start on 2023-01-18)_
 
More specifically, Google UMP supposedly integrates well with Google's own advertising solution AdMob:

> We've also simplified the process of setting up consent forms and listing ad partners.
> All of these configurations can now conveniently be handled in AdMob Privacy & messaging.
>
> _(copied from: https://developers.google.com/admob/ump/android/quick-start on 2023-01-18)_

### Google UMP version history
Google UMP has first been released in July 2020. More than a full year later, a single update was released.
At the time of writing (January 2023), Google UMP has not been updated for more than 2.5 years.

_(source: https://developers.google.com/admob/ump/android/release-notes)_

At some point throughout its history, Google UMP was also known as **Google Funding Choices**. This name seems to have
been dropped, but you are still in the right place.

## What are Google UMP's shortcomings?

### Easily achievable configuration states that lead to no ads being shown, undetectable for developers
One big issue with using Google UMP in combination with Google AdMob is that it easily leads to configuration states
that will prevent Google AdMob from showing any, even so-called "non-personalized", ads.

If necessary, Google UMP will show a dialog to users that allows them to either give full consent by clicking on a button
with the label "Consent", or by configuring their consent individually by clicking a button with the label "Manage options".
Giving full consent will typically lead to personalized ads being shown.

Trying to configure consent individually, however, will bring up a secondary screen with "purposes" as defined in the
"IAB Europe Transparency & Consent Framework" (TCF)
([_link_](https://iabeurope.eu/iab-europe-transparency-consent-framework-policies/#Appendix_A_Purposes_and_Features_Definitions)).
Each "purpose" is presented with its "name" and "user-friendly text", and is followed by up to two switches with the labels
"Consent" (defaults to off) and optionally "Legitimate Interest" (defaults to on where present).
The whole list takes up about four to five full screens on an average smartphone display.

Following this list is another text button with the label "Vendor preferences". Clicking on this button brings up a
tertiary screen that lists _all_ potentially several dozen vendors that the developer activated in the AdMob settings
for the specific app. Each vendor is listed with its full name, a short summary of how cookies might be stored and again up to
two switches ("Consent" defaulting to off; "Legitimate Interest" defaulting to on). This vendor list is not sorted
alphabetically, but by an internal "vendor ID" that is not shown in this list.

To manually configure these options in a way that leads to at least some ads being shown, a user would have to:
1. Click on "Manage options"
2. Toggle at least one specific switch for "Consent" to on, or three if more valuable personalized ads should be shown ([_link_](https://support.google.com/admob/answer/9760862))
3. Toggle four specific switches for "Legitimate Interest" to on if not already activated
4. Click through to the tertiary screen
5. In that list, find all vendors that the developer wants to use as "mediation partners" to display ads (how?), and give consent and/or allow legitimate interest

It is near-impossible for even a well-meaning user to perform this configuration task correctly, much less with no good
way for the developer to guide the user through this process. It is very easy for users to **deliberately** configure these
options in a way that prevents all ads from being shown.

**Google UMP offers no functionality to even detect whether the current configuration state would allow certain ads or not.**

Workaround: [TODO]

### Easily forgotten configuration states
While the UMP dialog is presented automatically once per user, developers are supposed to add an option that allows users
to bring up the dialog again at a later date. If a user chooses to do so, the dialog will **not** be in a state that
reflects previously given consent, but will instead be loaded with the aforementioned defaults.

In a hypothetical scenario where a user wants to retract some individual consent that was previously given,
for example for one of many vendors, the user would have to perform the full configuration again. This is annoying and
error-prone, and will generally lead to fewer ads being shown to users.

**Google representatives claims that this works as intended. No workarounds exist.** ([_link_](https://groups.google.com/g/google-admob-ads-sdk/c/UcveWmtBm4Q/m/T4avskCzCwAJ))

### AdMob error 3.3
The "IAB Europe Transparency & Consent Framework" explicitly states in its "Chapter II: Policies for CMPs" that:

> A CMP will remind the user of their right to withdraw consent and/or the right to object to processing at least
> every 13 months with respect to any Vendor and Purpose.
>
> _(copied from: https://iabeurope.eu/iab-europe-transparency-consent-framework-policies/#_Chapter_II_Policies_for_CMPs on 2023-01-18)_

It is the responsibility of Google UMP to keep track of the date the user last gave consent to processing their data,
and bring up the consent dialog again at least every 13 months. Google UMP fails to do that and instead serves consent
data that needs to be considered outdated. Other products, including AdMob, correctly detect this outdated data and log an error.

On its help page for error codes, Google AdMob helpfully states that:

> You are required by IAB TCF policy to remind users about their consent choices at least once every 13 months.
> If the consent decision is more than 13 months old, the TC string will no longer be considered valid by Google
> and Google will not serve ads to that user. We suggest that you work with your CMP to remind users about their
> consent choices before the 13-month limit is reached.
>
> _(copied from: https://support.google.com/admob/answer/9999955 on 2023-01-18)_

At the same time, Google representatives in the official ["Google Mobile Ads SDK Developers" forum](https://groups.google.com/g/google-admob-ads-sdk/)
keep insisting that this error is "out of [their] scope" to get fixed, and that developers should keep track of the date
of last consent using some out-of-band means themselves, to then manually delete consent data to get Google UMP to
re-display the consent form: ([_link 1_](https://groups.google.com/g/google-admob-ads-sdk/c/MUPYWR-ZlhQ/),
[_2_](https://groups.google.com/g/google-admob-ads-sdk/c/zVk9dUVerU8/),
[_3_](https://groups.google.com/g/google-admob-ads-sdk/c/tERL8SFnhEY/))

Workaround: detect outdated consent

## Should I even be using Google's UMP?
**tl;dr - NO**

If you are still undecided and haven't yet invested into either AdMob generally or UMP specifically, you should find another
solution. Other CMP solutions generally seem to work better than UMP, and even Google representatives have suggested to
switch to a different Content Management Platform [at the end of a lengthy discussion](https://groups.google.com/g/google-admob-ads-sdk/c/UcveWmtBm4Q/m/ozd595HtAAAJ):

> If you feel that your example is sufficiently frustrating users or causing other issues for your application,
> we suggest switching to another CMP that handles privacy regulations differently.

Only if you _need_ to work within Google's framework of AdMob, UMP and related SDKs, and can't switch to a different
solution easily, the workarounds in this repository might help ease the pain a bit.

## Legalese
All original content in this repository is published under the **CC0 1.0 Universal** license. You can find the full
legal text in the LICENSE file of this repository - but what it means in a nutshell is that you can use code in this
repository however you like, including in commercial products, but without any warranties of any kind.

When doing so, you do **not** need to mention me or this repository in your finished product (e.g. an "About" screen in your app).
However, if you consider any of the workarounds published here to be useful, feel free to raise awareness about these
issues by sharing this repository with friends and colleagues.

Quotes marked as such in this and other text documents in this repository are explicitly not meant to fall under the above license.
I do not claim ownership of any of these quotes. They are reproduced here to allow conversation about the mentioned
issues, and their original source is given in each case.

_Google_, _AdMob_, _Android_ and probably other names are trademarked by Google LLC. They are necessarily used here to
discuss the individual products and their problems. No trademark infringement is intended.

## Pull requests
I am happy to accept pull requests, especially to translate existing workarounds to other languages and/or platforms.
Just follow naming, directory structure and coding style of existing workarounds as far as sensible,
and we'll talk about the rest.