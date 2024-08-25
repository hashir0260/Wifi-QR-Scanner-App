package com.wifi.wifiqrcodepasswordscanner.ui.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.FileProvider
import com.wifi.wifiqrcodepasswordscanner.BuildConfig
import com.wifi.wifiqrcodepasswordscanner.R
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivityShowQractivityBinding
import com.wifi.wifiqrcodepasswordscanner.extensions.setSafeOnClickListener
import com.wifi.wifiqrcodepasswordscanner.extensions.showToast
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream

class ShowQRCodeActivity : AppCompatActivity() {
    lateinit var binding: ActivityShowQractivityBinding
    private var qrBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowQractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFun()
        setClicks()
    }

    private fun initFun() {
        binding.textView.text = getString(R.string.qr_code)
        qrBitmap = intent.getByteArrayExtra("QR_CODE_BITMAP")?.let { byteArray ->
            ByteArrayInputStream(byteArray).use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }
        if (qrBitmap != null) {
            binding.imgQR.setImageBitmap(qrBitmap)
        }
    }

    private fun setClicks() {
        binding.imgBackExit.setSafeOnClickListener {
            finish()
        }

        binding.btnShareQR.setSafeOnClickListener {
            shareQrFun()
        }
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
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content) + "\n" + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setDataAndType(contentUri, contentResolver.getType(contentUri))
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                }
                startActivity(Intent.createChooser(shareIntent, "Choose an app"))
            }
        }
    }
}
