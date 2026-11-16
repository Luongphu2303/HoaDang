package com.universal.callredirect

import android.content.Context
import android.net.Uri
import android.telecom.CallRedirectionService
import android.telecom.PhoneAccountHandle

class UniversalRedirectService : CallRedirectionService() {

    override fun onPlaceCall(
        handle: Uri,
        initialPhoneAccount: PhoneAccountHandle,
        allowInteractiveResponse: Boolean
    ) {
        val prefs = getSharedPreferences("RedirectPrefs", Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean("is_enabled", false)
        val targetNumber = prefs.getString("target_number", "")?.trim() ?: ""
        val originalNumber = handle.schemeSpecificPart ?: ""

        if (isEnabled && targetNumber.isNotEmpty() && originalNumber != targetNumber) {
            val newUri = Uri.fromParts("tel", targetNumber, null)
            redirectCall(newUri, initialPhoneAccount, false)
        } else {
            placeCallUnmodified()
        }
    }
}
