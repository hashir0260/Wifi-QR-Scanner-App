package com.wifi.wifiqrcodepasswordscanner.ui.activities


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wifi.wifiqrcodepasswordscanner.R
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivityCreateWifiQrBinding
import com.wifi.wifiqrcodepasswordscanner.extensions.setSafeOnClickListener


class CreateWifiQRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateWifiQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateWifiQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFun()
        setClicks()
    }
    private fun initFun() {

        val adapter = ArrayAdapter.createFromResource(this, R.array.auth_types, R.layout.spinner_item)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.authTypeSpinner.adapter = adapter

        binding.textView.text = getString(R.string.create)
    }

    private fun setClicks() {
        binding.generateQRButton.setSafeOnClickListener{
            generateQRCode()
        }

        binding.imgBackExit.setSafeOnClickListener {
            finish()
        }
    }

    private fun generateQRCode() {
        val ssid: String = binding.ssidEditText.text.toString()
        val password: String = binding.passwordEditText.text.toString()
        val authType: String = binding.authTypeSpinner.selectedItem.toString()
        if (TextUtils.isEmpty(ssid)) {
            Toast.makeText(this, "SSID cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.error = "Password cannot be empty";
            binding.passwordEditText.requestFocus()
            return
        }
        val qrContent = "WIFI:T:$authType;S:$ssid;P:$password;;"
        val intent = Intent(this,CreateAllQRActivity::class.java)
        intent.putExtra("isUrlType","qr")
        intent.putExtra("textData",qrContent)
        startActivity(intent)
        finish()
    }
}