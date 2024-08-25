package com.wifi.wifiqrcodepasswordscanner.ui.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.wifi.wifiqrcodepasswordscanner.BuildConfig
import com.wifi.wifiqrcodepasswordscanner.R
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivityScanResultBinding
import com.wifi.wifiqrcodepasswordscanner.extensions.beGone
import com.wifi.wifiqrcodepasswordscanner.extensions.copyToClipboard
import com.wifi.wifiqrcodepasswordscanner.extensions.createQRCode
import com.wifi.wifiqrcodepasswordscanner.extensions.mScannedResultString
import com.wifi.wifiqrcodepasswordscanner.extensions.qrBitmap
import com.wifi.wifiqrcodepasswordscanner.extensions.setSafeOnClickListener
import com.wifi.wifiqrcodepasswordscanner.extensions.showToast
import java.io.File
import java.io.FileOutputStream

class ResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanResultBinding
    private var qrType: String = ""
    private var filePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFun()
        setClicks()
    }

    private fun initFun() {
        qrType = intent.getStringExtra("qrType").toString()
        binding.textView.text = "Scanned Results"
        val scannedResult: String = mScannedResultString.toString()

        if (isWifiQR(scannedResult)) {
            parseAndDisplayWifiInfo(scannedResult)
        } else {
            binding.connectWifiButton.text = "Share"
            if (qrType == "barcode") {
                createQRCode(scannedResult, "barcode")
            } else {
                createQRCode(scannedResult, "QR")
            }

            binding.passwordTextView.text = scannedResult
            binding.ssidTextView.beGone()
            binding.btnCopy.beGone()
            binding.pastePass.beGone()
            binding.authTypeTextView.beGone()
        }

        if (qrBitmap != null) {
            binding.qrCodeImageView.setImageBitmap(qrBitmap)
        }
    }

    private fun setClicks() {
        binding.connectWifiButton.setSafeOnClickListener {
            val scannedResult: String = mScannedResultString.toString()
            if (isWifiQR(scannedResult)) {
                val wifiSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(wifiSettingsIntent)
            } else {
                shareQrFun()
            }
        }

        // Optional: Ensure text is copyable on click
        binding.btnCopy.setSafeOnClickListener {
            copyToClipboard(binding.passwordTextView.text.toString())
        }

        binding.imgBackExit.setSafeOnClickListener {
            finish()
        }
    }

    private fun isWifiQR(result: String): Boolean {
        return result.startsWith("WIFI:")
    }

    private fun parseAndDisplayWifiInfo(result: String) {
        createQRCode(result, "QR")
        val wifiInfo = result.substring(5).split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var ssid: String? = null
        var password: String? = null
        var authType: String? = null
        for (info in wifiInfo) {
            when {
                info.startsWith("S:") -> ssid = info.substring(2)
                info.startsWith("P:") -> password = info.substring(2)
                info.startsWith("T:") -> authType = info.substring(2)
            }
        }
        binding.ssidTextView.text = if (ssid != null) "SSID: $ssid" else "SSID: N/A"
        binding.passwordTextView.text = if (password != null) "Password: $password" else "Password: N/A"
        binding.authTypeTextView.text = if (authType != null) "Auth Type: $authType" else "Auth Type: N/A"
        Toast.makeText(this, "WiFi QR Code Detected", Toast.LENGTH_LONG).show()

        copyToClipboard(binding.passwordTextView.text.toString())
    }

    private fun shareQrFun() {
        if (qrBitmap != null) {
            try {
                val cachePath = File(cacheDir, "images")
                cachePath.mkdirs()
                val stream = FileOutputStream("$cachePath/image.png")
                qrBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                stream.close()
            } catch (e: ActivityNotFoundException) {
                showToast("Install Latest Gmail Version to share")
            }

            val imagePath = File(cacheDir, "images")
            val newFile = File(imagePath, "image.png")
            val contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", newFile)

            if (contentUri != null) {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content) + "\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareIntent.setDataAndType(contentUri, contentResolver.getType(contentUri))
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                startActivity(Intent.createChooser(shareIntent, "Choose an app"))
            }
        }
    }
}
