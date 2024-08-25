package com.wifi.wifiqrcodepasswordscanner.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivityTwitterQrBinding
import java.io.ByteArrayOutputStream

class TwitterQRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTwitterQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTwitterQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBackExit.setOnClickListener {
            finish()
        }

        binding.generateTwitterQRButton.setOnClickListener {
            val text = binding.twitterEditText.text.toString()
            if (text.isNotEmpty()) {
                if (text.contains("x") && text.contains(".com")) {
                    val qrBitmap = generateQRCode(text)
                    if (qrBitmap != null) {
                        val byteArray = bitmapToByteArray(qrBitmap)
                        // Pass the generated QR code bitmap as a byte array to ShowQRCodeActivity
                        val intent = Intent(this, ShowQRCodeActivity::class.java).apply {
                            putExtra("QR_CODE_BITMAP", byteArray)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please enter a valid Twitter X URL", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a Twitter X URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateQRCode(text: String): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400)
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
