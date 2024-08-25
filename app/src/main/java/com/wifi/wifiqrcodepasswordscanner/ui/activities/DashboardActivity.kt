package com.wifi.wifiqrcodepasswordscanner.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.wifi.wifiqrcodepasswordscanner.R
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivityDashboardBinding
import com.wifi.wifiqrcodepasswordscanner.databinding.DialogExitBinding
import com.wifi.wifiqrcodepasswordscanner.extensions.callForPermissionsExt
import com.wifi.wifiqrcodepasswordscanner.extensions.setSafeOnClickListener
import com.wifi.wifiqrcodepasswordscanner.extensions.showToast

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClicks()
    }

    private fun setClicks() {
        binding.imgBackExit.setSafeOnClickListener {
            showCustomExitDialog()
        }

        binding.imgWifiScanQR.setSafeOnClickListener {
            callForPermissionsExt({
                val intent = Intent(this@DashboardActivity, ScanWifiQrActivity::class.java)
                startActivity(intent)
            },{
                showToast("Grant Permission To Proceed")
            })
        }

        binding.scanId.setSafeOnClickListener {
            callForPermissionsExt({
                val intent = Intent(this@DashboardActivity, ScanWifiQrActivity::class.java)
                startActivity(intent)
            },{
                showToast("Grant Permission To Proceed")
            })
        }

        binding.imgWifiCreate.setSafeOnClickListener {
            val intent = Intent(this@DashboardActivity, CreateWifiQRActivity::class.java)
            startActivity(intent)
        }

        binding.imgContactCreate.setSafeOnClickListener {
            val intent = Intent(this@DashboardActivity, ContactQRActivity::class.java)
            startActivity(intent)
        }

        binding.imgText.setSafeOnClickListener {
            val intent = Intent(this@DashboardActivity, TextQRActivity::class.java)
            startActivity(intent)
        }

        binding.imgUrlQR.setSafeOnClickListener {
            val intent = Intent(this@DashboardActivity, URLQRActivity::class.java)
            startActivity(intent)
        }

        binding.imgEmailQR.setSafeOnClickListener {
            val intent = Intent(this@DashboardActivity, EmailQRActivity::class.java)
            startActivity(intent)
        }

        binding.imgWhatsappQR.setSafeOnClickListener {
            val intent = Intent(this@DashboardActivity, WhatsappQRActivity::class.java)
            startActivity(intent)
        }

        binding.imgInstagramQR.setSafeOnClickListener {
            val intent = Intent(this@DashboardActivity, InstagramQRActivity::class.java)
            startActivity(intent)
        }

        binding.imgTwitterQR.setSafeOnClickListener {
            val intent = Intent(this@DashboardActivity, TwitterQRActivity::class.java)
            startActivity(intent)
        }

        binding.settingsId.setSafeOnClickListener {
            val intent = Intent(this@DashboardActivity, SettingsQRActivity::class.java)
            startActivity(intent)
        }

        // Add the click listener for generate_qr ImageView
        binding.generateQr.setSafeOnClickListener {
            val intent = Intent(this@DashboardActivity, TextQRActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showCustomExitDialog() {
        val dialogBinding = DialogExitBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(this, R.style.CustomExitDialogTheme)
            .setView(dialogBinding.root)
            .setCancelable(false) // Prevent dialog from being dismissed by back button
            .create()

        dialogBinding.btnYes.setOnClickListener {
            dialog.dismiss()
            finishAffinity() // Close the app
        }

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog
        }

        // Allow dialog to be dismissed by touching outside
        dialog.setCanceledOnTouchOutside(true)

        // Set the dialog background to transparent to apply the custom drawable
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Configure dialog window layout parameters
        val layoutParams = WindowManager.LayoutParams().apply {
            copyFrom(dialog.window?.attributes)
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        dialog.window?.attributes = layoutParams

        dialog.show()
    }

    override fun onBackPressed() {
        showCustomExitDialog() // Show the custom exit dialog when the back button is pressed
    }
}
