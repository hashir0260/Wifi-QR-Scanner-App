package com.wifi.wifiqrcodepasswordscanner.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wifi.wifiqrcodepasswordscanner.R
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivitySplashBinding
import com.wifi.wifiqrcodepasswordscanner.extensions.setSafeOnClickListener

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setClicks()

    }

    private fun setClicks() {
        binding.btnLetsStart.setSafeOnClickListener {
            val intent =  Intent(this,DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}