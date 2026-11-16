package com.universal.callredirect

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private val ROLE_REQUEST_CODE = 1001
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("RedirectPrefs", Context.MODE_PRIVATE)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 80, 60, 60)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val title = TextView(this).apply {
            text = "CHUYỂN HƯỚNG CUỘC GỌI ĐI"
            textSize = 20f
            setPadding(0, 0, 0, 40)
        }

        statusText = TextView(this).apply {
            textSize = 15f
            setPadding(0, 0, 0, 40)
        }

        val btnGrantRole = Button(this).apply {
            text = "1. CẤP QUYỀN HỆ THỐNG (BẮT BUỘC)"
            setOnClickListener { requestCallRedirectionRole() }
        }

        val inputNumber = EditText(this).apply {
            hint = "Nhập số đích (Ví dụ: 198)"
            setText(prefs.getString("target_number", "198"))
            textSize = 18f
            setPadding(30, 40, 30, 40)
        }

        val switchEnable = Switch(this).apply {
            text = "Kích hoạt chuyển hướng tự động  "
            textSize = 17f
            isChecked = prefs.getBoolean("is_enabled", false)
            setPadding(0, 40, 0, 40)
        }

        val btnSave = Button(this).apply {
            text = "2. LƯU CẤU HÌNH"
            setOnClickListener {
                val num = inputNumber.text.toString().trim()
                prefs.edit()
                    .putString("target_number", num)
                    .putBoolean("is_enabled", switchEnable.isChecked)
                    .apply()
                Toast.makeText(
                    this@MainActivity,
                    if (switchEnable.isChecked) "Đã BẬT chuyển hướng sang: $num" else "Đã TẮT chuyển hướng",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        layout.addView(title)
        layout.addView(statusText)
        layout.addView(btnGrantRole)
        layout.addView(inputNumber)
        layout.addView(switchEnable)
        layout.addView(btnSave)

        setContentView(layout)
        updateRoleStatus()
    }

    private fun updateRoleStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            val isHeld = roleManager.isRoleHeld(RoleManager.ROLE_CALL_REDIRECTION)
            if (isHeld) {
                statusText.text = "Trạng thái quyền: ĐÃ SẴN SÀNG HOẠT ĐỘNG"
                statusText.setTextColor(Color.parseColor("#2E7D32"))
            } else {
                statusText.text = "Trạng thái quyền: CHƯA CẤP QUYỀN (Bấm nút 1 bên dưới)"
                statusText.setTextColor(Color.parseColor("#C62828"))
            }
        }
    }

    private fun requestCallRedirectionRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_REDIRECTION)) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_REDIRECTION)
                startActivityForResult(intent, ROLE_REQUEST_CODE)
            } else {
                Toast.makeText(this, "Máy không hỗ trợ Role này", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ROLE_REQUEST_CODE) {
            updateRoleStatus()
        }
    }
}
