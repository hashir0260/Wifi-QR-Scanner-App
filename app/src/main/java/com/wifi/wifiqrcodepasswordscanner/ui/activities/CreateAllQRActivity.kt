package com.wifi.wifiqrcodepasswordscanner.ui.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.wifi.wifiqrcodepasswordscanner.BuildConfig
import com.wifi.wifiqrcodepasswordscanner.R
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivityCreateAllQrBinding
import com.wifi.wifiqrcodepasswordscanner.extensions.createQRCode
import com.wifi.wifiqrcodepasswordscanner.extensions.qrBitmap
import com.wifi.wifiqrcodepasswordscanner.extensions.setSafeOnClickListener
import com.wifi.wifiqrcodepasswordscanner.extensions.showToast
import java.io.File
import java.io.FileOutputStream

class CreateAllQRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAllQrBinding
    private var isUrlType:String = ""
    private var qrText:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAllQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFun()
        setClicks()
    }

    private fun initFun() {
        isUrlType = intent.getStringExtra("isUrlType").toString()
        qrText = intent.getStringExtra("textData").toString()
        binding.textView.text = "Create QR"

        when (isUrlType) {
            "url" -> {
                binding.txtUrl.text = getString(R.string.url)
            }
            "number" -> {
                binding.txtUrl.text = getString(R.string.phone)
            }
            else -> {
                binding.txtUrl.text = "QR Code"
                generateQRCode()
            }

        }
        if (isUrlType=="barcode"){
            createQRCode(qrText,"barcode")
        }else{
            createQRCode(qrText,"QR")
        }
        binding.txtQRFromText.text = qrText
        binding.imgQR.setImageBitmap(qrBitmap)
    }

    private fun setClicks(){
        binding.imgBackExit.setSafeOnClickListener {
            finish()
        }
        binding.btnShare.setSafeOnClickListener {
            shareQrFun()
        }
    }


    private fun shareQrFun() {
        if (qrBitmap!=null){
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
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content) + "\n" + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
                shareIntent.setDataAndType(contentUri, contentResolver.getType(contentUri))
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                startActivity(Intent.createChooser(shareIntent, "Choose an app"))
            }
        }
    }


    private fun generateQRCode() {

        val qrContent = qrText
        val qrCodeWriter = QRCodeWriter()
        try {
            createQRCode(qrText,"QR")
//            val bitmap: Bitmap = toBitmap(qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 500, 500))
//            binding.imgQR.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toBitmap(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

}