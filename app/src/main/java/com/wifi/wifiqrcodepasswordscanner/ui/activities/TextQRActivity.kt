package com.wifi.wifiqrcodepasswordscanner.ui.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wifi.wifiqrcodepasswordscanner.R
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivityTextQrBinding
import com.wifi.wifiqrcodepasswordscanner.extensions.setSafeOnClickListener
import com.wifi.wifiqrcodepasswordscanner.extensions.showToast
import android.annotation.SuppressLint as SuppressLint1

class TextQRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFun()
        textChangeFun()
        setClicks()
    }

    private fun initFun() {
        binding.textView.text = getString(R.string.text_qr)
    }

    @SuppressLint1("SetTextI18n")
    private fun setClicks() {
        binding.imgBackExit.setSafeOnClickListener {
            finish()
        }

        binding.imgClear.setSafeOnClickListener {
            binding.edtTextQR.setText("")
        }

        binding.btnCreateQR.setSafeOnClickListener {
            if (binding.edtTextQR.text.isNotEmpty()) {
                val intent = Intent(this,CreateAllQRActivity::class.java)
                intent.putExtra("isUrlType","text")
                intent.putExtra("textData",binding.edtTextQR.text.toString())
                startActivity(intent)
                finish()
            }
        }

        binding.imgPasteFrom.setSafeOnClickListener {
            pasteFun()
        }
    }

    private fun pasteFun() {
        try {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val item = clipboard.primaryClip!!.getItemAt(0)
            if (item.text != null) {
                binding.edtTextQR.text.insert(binding.edtTextQR.selectionStart, item.text)
            }
            binding.edtTextQR.setSelection(binding.edtTextQR.text.lastIndex + 1)
        } catch (e: NullPointerException) {
            showToast("No Copied Data...")
        }
    }

    private fun textChangeFun() {
        binding.edtTextQR.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val enteredText = s.toString()
                if (enteredText.isEmpty()) {
                    binding.edtTextQR.hint = "Enter Text Here"
                    binding.imgClear.visibility = View.INVISIBLE
                    binding.btnCreateQR.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@TextQRActivity, R.color.black_light))
                } else {
                    binding.imgClear.visibility = View.VISIBLE
                    binding.btnCreateQR.setBackgroundResource(R.drawable.button_bg)
                    binding.btnCreateQR.backgroundTintList = ContextCompat.getColorStateList(this@TextQRActivity, R.color.yellow)
                }
            }
        })
    }
}
