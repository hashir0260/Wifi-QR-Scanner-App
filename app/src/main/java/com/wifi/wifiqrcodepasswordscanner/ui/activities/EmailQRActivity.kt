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
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivityEmailQrBinding
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivityUrlQrBinding
import com.wifi.wifiqrcodepasswordscanner.extensions.showToast

class EmailQRActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmailQrBinding
    private var isUrlType:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityEmailQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFun()
        setClicks()
        setTextChange()
    }

    private fun initFun() {
        binding.textView.text = "Create"
        isUrlType = intent.getStringExtra("isUrlType").toString()
    }

    private fun setClicks() {
        binding.imgBackExit.setOnClickListener {
            onBackPressed()
        }

        binding.btnCancel.setOnClickListener {
            binding.edtUrl.setText("")
            binding.btnCancel.visibility = View.INVISIBLE
        }

        binding.btnCreateUrl.setOnClickListener {
            createQrFun()
        }
    }

    private fun createQrFun() {
        if (binding.edtUrl.text.equals("") || !binding.edtUrl.text.contains("@")){
            showToast("Enter Content First or not Proper Email")
            return
        }else{
            val intent = Intent(this,CreateAllQRActivity::class.java)
            intent.putExtra("isUrlType",isUrlType)
            intent.putExtra("textData",binding.edtUrl.text.toString())
            startActivity(intent)
            finish()
        }
    }


    private fun setTextChange() {
        binding.edtUrl.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnCancel.visibility = if (s!!.isNotEmpty()) View.VISIBLE else View.INVISIBLE

                if (s.trim().isEmpty()) {
                    binding.btnCreateUrl.apply {
                        backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey))
                    }
                } else {
                    binding.btnCreateUrl.setBackgroundResource(R.drawable.button_bg)
                    binding.btnCreateUrl.backgroundTintList = ContextCompat.getColorStateList(this@EmailQRActivity, R.color.yellow)
                }
            }
        })
    }
}