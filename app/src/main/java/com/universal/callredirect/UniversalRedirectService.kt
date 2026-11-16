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
        try {
            val prefs = getSharedPreferences("RedirectPrefs", Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("is_enabled", false)
            val targetNumber = prefs.getString("target_number", "198")?.trim() ?: "198"
            val originalNumber = handle.schemeSpecificPart ?: ""

            // Nếu bật chuyển hướng và số gọi đi khác số đích thì tiến hành đổi hướng
            if (isEnabled && targetNumber.isNotEmpty() && originalNumber != targetNumber) {
                val newUri = Uri.fromParts("tel", targetNumber, null)
                redirectCall(newUri, initialPhoneAccount, false)
            } else {
                // Nếu tắt hoặc gọi chính số đích thì chạy bình thường không can thiệp
                placeCallUnmodified()
            }
        } catch (e: Exception) {
            // Chống crash tuyệt đối, nếu lỗi tự động gọi bình thường
            placeCallUnmodified()
        }
    }
}
