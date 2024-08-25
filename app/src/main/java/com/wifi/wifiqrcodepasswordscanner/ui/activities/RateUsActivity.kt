package com.wifi.wifiqrcodepasswordscanner.ui.activities

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wifi.wifiqrcodepasswordscanner.R

class RateUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a dialog with a custom theme
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.dialogue_rate_us)

        // Ensure the dialog has rounded corners and proper background
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Ensures transparency

        // Find the RatingBar and Submit Button inside the dialog
        val ratingBar = dialog.findViewById<RatingBar>(R.id.ratingBar)
        val submitButton = dialog.findViewById<Button>(R.id.btnRateUsSubmit)

        // Handle the submit button click
        submitButton.setOnClickListener {
            val rating = ratingBar.rating
            // Process the rating (e.g., save it, send to server, etc.)
            handleRatingSubmission(rating)
            // Dismiss the dialog after submission
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun handleRatingSubmission(rating: Float) {
        // Here you can process the rating, such as sending it to a server or saving it locally
        Toast.makeText(this, "Thank you for your rating: $rating", Toast.LENGTH_SHORT).show()
        // Finish the activity after submission
        finish()
    }
}
