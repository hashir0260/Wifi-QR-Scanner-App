package com.wifi.wifiqrcodepasswordscanner.ui.activities

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.wifi.wifiqrcodepasswordscanner.R
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivityContactQractivityBinding
import com.wifi.wifiqrcodepasswordscanner.extensions.showToast

class ContactQRActivity : AppCompatActivity() {
    lateinit var binding: ActivityContactQractivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactQractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFun()
        setClicks()
        textChangeFun()
    }

    private fun initFun() {
        binding.textView.text = getString(R.string.phone)
        binding.txtType.text = getString(R.string.phone)
    }

    private fun setClicks() {
        binding.imgCancelNumber.setOnClickListener {
            binding.edtNumber.setText("")
            binding.imgCancelNumber.visibility = View.INVISIBLE
        }

        binding.imgBackExit.setOnClickListener{
            finish()
        }

        binding.btnCreate.setOnClickListener {
            createQrFun()
        }
    }

    private fun textChangeFun() {
        binding.edtNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.imgCancelNumber.visibility = if (s!!.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                if (s.trim().isEmpty()) {
                    binding.btnCreate.apply {
                        backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey))
                    }
                } else {
                    binding.btnCreate.setBackgroundResource(R.drawable.button_bg)
                    binding.btnCreate.backgroundTintList = ContextCompat.getColorStateList(this@ContactQRActivity, R.color.yellow)
                }
            }
        })
    }

    private fun createQrFun() {
        binding.textView.text.toString()
        binding.edtNumber.text.toString()

        if (binding.edtNumber.text.isNotEmpty()){
            val ccp= binding.ccp.selectedCountryCode.toString()
            val number= binding.edtNumber.text.toString()
            val intent = Intent(this, CreateAllQRActivity::class.java)
            intent.putExtra("isUrlType","number")
            intent.putExtra("textData",ccp+number)
            startActivity(intent)
            finish()
        }else{
            showToast("Enter Content First...")
        }
    }
}