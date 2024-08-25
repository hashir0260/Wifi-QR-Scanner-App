package com.wifi.wifiqrcodepasswordscanner.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wifi.wifiqrcodepasswordscanner.BuildConfig
import com.wifi.wifiqrcodepasswordscanner.R
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivitySettingsQrBinding
import com.wifi.wifiqrcodepasswordscanner.extensions.setSafeOnClickListener

class SettingsQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsQrBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        // Load the switch states
        binding.switchVibrate.isChecked = sharedPreferences.getBoolean("vibrate_on_scan", false)
        binding.switchBeep.isChecked = sharedPreferences.getBoolean("beep_on_scan", false)

        setClicks()
        setupSwitches()
    }

    private fun setClicks() {
        // Handle back navigation
        binding.imgBackDashboard.setSafeOnClickListener {
            finish()
        }

        // Handle showing the Rate Us dialog
        binding.constraintRateUs.setSafeOnClickListener {
            showRateUsDialog()
        }

        // Handle sharing the app
        binding.constraintShareApp.setSafeOnClickListener {
            shareApp() // Share the app using the defined function
        }

        // Handle opening the privacy policy
        binding.constraintPrivacyPolicy.setSafeOnClickListener {
            openPrivacyPolicy()
        }

        // Handle exiting the app
        binding.constraintExitApp.setSafeOnClickListener {
            finishAffinity() // This will close the app
        }
    }

    private fun openPrivacyPolicy() {
        try {
            val uri: Uri = Uri.parse("https://sites.google.com/view/wifiqrscannerapp/home")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.web), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRateUsDialog() {
        // Inflate the custom dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialogue_rate_us, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        val submitButton = dialogView.findViewById<Button>(R.id.btnRateUsSubmit)

        // Create and configure the AlertDialog
        val dialog = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Set the dialog background to transparent to apply the custom drawable
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set the submit button click listener
        submitButton.setOnClickListener {
            val rating = ratingBar.rating
            handleRatingSubmission(rating)
            dialog.dismiss()  // Close the dialog after submission
        }

        // Configure dialog window layout parameters
        val layoutParams = WindowManager.LayoutParams().apply {
            copyFrom(dialog.window?.attributes)
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        dialog.window?.attributes = layoutParams

        // Show the dialog
        dialog.show()
    }

    private fun handleRatingSubmission(rating: Float) {
        // Process the rating (e.g., save it, send to server, etc.)
        Toast.makeText(this, "Thank you for your rating: $rating", Toast.LENGTH_SHORT).show()
    }

    private fun setupSwitches() {
        binding.switchVibrate.setOnCheckedChangeListener { _, isChecked ->
            // Save the vibration switch state in SharedPreferences
            with(sharedPreferences.edit()) {
                putBoolean("vibrate_on_scan", isChecked)
                apply()
            }
        }

        binding.switchBeep.setOnCheckedChangeListener { _, isChecked ->
            // Save the beep switch state in SharedPreferences
            with(sharedPreferences.edit()) {
                putBoolean("beep_on_scan", isChecked)
                apply()
            }
        }
    }

    private fun shareApp() {
        try {
            val shareContent = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_content))
            intent.putExtra(Intent.EXTRA_TEXT, shareContent)
            startActivity(Intent.createChooser(intent, "Choose an app"))
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.no_share), Toast.LENGTH_SHORT).show()
        }
    }
}
