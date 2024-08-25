package com.wifi.wifiqrcodepasswordscanner.extensions

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.nabinbhandari.android.permissions.BuildConfig
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.wifi.wifiqrcodepasswordscanner.R
import java.util.EnumMap
import java.util.Locale


var mScannedResultString: String? = null

var Button.isButtonSelectedExtension: Boolean
    get() = tag as? Boolean ?: false
    set(value) {
        tag = value
    }

fun Button.animateButton() {
    val scaleValue = if (isButtonSelectedExtension) 1.2f else 1.0f
    animate().scaleX(scaleValue).scaleY(scaleValue).setDuration(200)
        .withEndAction {
            postDelayed({
                animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start()
                isButtonSelectedExtension = false
            }, 500)
        }
        .start()
}


fun Activity.permissionsFun(permissionGranted: () -> Unit,permissionDenied: () -> Unit) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    Permissions.check(
        this /*context*/,
        permissions,
        null,null,
        object : PermissionHandler() {
            override fun onGranted() {
                permissionGranted.invoke()
            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                super.onDenied(context, deniedPermissions)
                permissionDenied.invoke()
            }
        }

    )
}

fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("copied text", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(this, "Text copied.", Toast.LENGTH_SHORT).show()
}


fun Context.shareContent(shareBody: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_content))
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(intent, "Choose an app"))
    } catch (e: Exception) {
        Toast.makeText(this, getString(R.string.no_share), Toast.LENGTH_SHORT).show()
    }
}

private var textToSpeech: TextToSpeech? = null
fun Context.speakText(
    text: String,
    onInit: ((TextToSpeech) -> Unit)? = null
) {
    if (textToSpeech == null) {
        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale.ENGLISH
                textToSpeech?.setSpeechRate(1f)
                textToSpeech?.let {
                    onInit?.invoke(it)
                }
                textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }else{
        textToSpeech?.stop()
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

}



fun afterDelay(delayMillis: Long, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        action.invoke()
    }, delayMillis)
}

var mLastClickTime: Long = 0
fun View.setSafeOnClickListener(delay: Long = 1000, func: (view: View) -> Unit) {
    this.setOnClickListener {
        if (shouldAllowClick(delay).not())
            return@setOnClickListener
        func(this)
    }
}

fun shouldAllowClick(delay: Long = 600): Boolean {
    if (SystemClock.elapsedRealtime() - mLastClickTime < delay) {
        return false
    }
    mLastClickTime = SystemClock.elapsedRealtime()
    return true
}

fun View.beVisible(){
    this.visibility = View.VISIBLE
}
fun View.beInVisible(){
    this.visibility = View.INVISIBLE
}
fun View.beGone(){
    this.visibility = View.GONE
}



var ImageView.isSelectedExtension: Boolean
    get() = tag as? Boolean ?: false
    set(value) {
        tag = value
    }

fun ImageView.animateTapSelection() {
    val scaleValue = if (isSelectedExtension) 1.2f else 1.0f
    animate().scaleX(scaleValue).scaleY(scaleValue).setDuration(200)
        .withEndAction {
            postDelayed({
                animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                isSelectedExtension = false
            }, 500)
        }
        .start()
}


fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.let { focusedView ->
        imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
    }
}

fun Context.isNetworkAvailable(): Boolean {
    return try {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        activeNetworkInfo != null && activeNetworkInfo.isConnected
    } catch (ex: Exception) {
        false
    }
}

fun Context.clipboardFun(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("copied text", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(this, "Text copied.", Toast.LENGTH_SHORT).show()
}


var mToast: Toast? = null

fun Context.showToast(toastString: String) {
    if (mToast != null) {
        mToast!!.cancel()
    }
    mToast = Toast.makeText(this, toastString, Toast.LENGTH_SHORT)
    mToast!!.setGravity(Gravity.CENTER, 0, 0)
    mToast!!.show()
}


fun String.capitalizeFirstLetterOfEachWord(): String {
    val words = this.split(" ")
    val capitalizedWords = words.map { it.toLowerCase().capitalize() }
    return capitalizedWords.joinToString(" ")
}



fun Context.shareApp() {
    try {
        val shareContent = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_content))
        intent.putExtra(Intent.EXTRA_TEXT, shareContent)
        startActivity(Intent.createChooser(intent, "Choose an app"))
    } catch (e: Exception) {
        Toast.makeText(this, getString(R.string.no_share), Toast.LENGTH_SHORT).show()
    }
}

fun Context.moreApps() {
    try {
        val uri: Uri = Uri.parse(getString(R.string.more_apps_link))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        showToast("Phone not Compatible")
    }
}

var qrBitmap: Bitmap? =null
fun Activity.createQRCode(qrText: String, type: String): Bitmap? {
    return try {
        // Setting size of QR code
        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        val smallestDimension = if (width < height) width else height
        val charset = "UTF-8"
        val hintMap: MutableMap<EncodeHintType, ErrorCorrectionLevel> = EnumMap(EncodeHintType::class.java)
        hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L

        if (qrText.isNotBlank()) {
            createQRCodeText(qrText, charset, hintMap, smallestDimension, smallestDimension,type)
        } else {
            null
        }
    } catch (ex: Exception) {
        ex.message?.let {
            Log.e("QrGenerate", it)
        }
        null
    }
}

fun createQRCodeText(
    qrTextValue: String,
    charset: String,
    hintMap: MutableMap<EncodeHintType, ErrorCorrectionLevel>,
    smallestDimension: Int,
    smallestDimension1: Int,
    type: String
): Bitmap? {
    return try {
        val matrix = if (type == "QR") {
            MultiFormatWriter().encode(
                String(qrTextValue.toByteArray(charset(charset)), charset(charset)),
                BarcodeFormat.QR_CODE, smallestDimension, smallestDimension1, hintMap
            )
        } else {
            MultiFormatWriter().encode(
                String(qrTextValue.toByteArray(charset(charset)), charset(charset)),
                BarcodeFormat.CODE_128, smallestDimension, smallestDimension1, hintMap
            )
        }

        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        // All are 0, or black, by default
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (matrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        qrBitmap = bitmap
        bitmap
    } catch (er: java.lang.Exception) {
        er.message?.let { Log.e("QrGenerate", it) }
        null
    }
}



fun Context.callForPermissionsExt(
    permissionGranted: () -> Unit,
    permissionDenied: () -> Unit
) {

    val permissions= arrayOf(android.Manifest.permission.CAMERA)

    Permissions.check(this, permissions, null, null,
        object : PermissionHandler() {
            override fun onGranted() {
                permissionGranted.invoke()
            }

            override fun onDenied(
                context: Context?,
                deniedPermissions: java.util.ArrayList<String?>?
            ) {
                permissionDenied.invoke()
            }
        })
}


/*
fun Context.privacyPolicy() {
    try {
        val uri: Uri = Uri.parse(getString(R.string.privacy_link))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        showToast("Install Browser to Continue")
    }
}

var ratingsValue:Float= 1F
fun Activity.rateUsFun() {
    val dialogBinding = LayoutRateUsBinding.inflate(LayoutInflater.from(this))
    val alertDialog = AlertDialog.Builder(this)
        .setView(dialogBinding.root)
        .setCancelable(false)
        .create()

    dialogBinding.ratingBar.onRatingBarChangeListener =
        RatingBar.OnRatingBarChangeListener { _, rating, _ ->
            ratingsValue = rating
        }

    dialogBinding.btnRateUsSubmit.setSafeOnClickListener{
        alertDialog.dismiss()
        if (ratingsValue > 4) {
            alertDialog.dismiss()
            val uri: Uri =
                Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                    )
                )
            }
        } else {
            alertDialog.dismiss()
            Toast.makeText(this, "Thank you For Your Feedback", Toast.LENGTH_SHORT).show()
        }
    }

    alertDialog.setCanceledOnTouchOutside(false)
    alertDialog.setCancelable(false)

    val layoutParams = WindowManager.LayoutParams().apply {
        copyFrom(alertDialog.window?.attributes)
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
    }
    alertDialog.window?.attributes = layoutParams
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.show()
}


var qrBitmap:Bitmap? =null
fun Activity.createQRCode(qrText: String, type: String): Bitmap? {
    return try {
        // Setting size of QR code
        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        val smallestDimension = if (width < height) width else height
        val charset = "UTF-8"
        val hintMap: MutableMap<EncodeHintType, ErrorCorrectionLevel> = EnumMap(EncodeHintType::class.java)
        hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L

        if (qrText.isNotBlank()) {
            createQRCodeText(qrText, charset, hintMap, smallestDimension, smallestDimension,type)
        } else {
            null
        }
    } catch (ex: Exception) {
        ex.message?.let {
            Log.e("QrGenerate", it)
        }
        null
    }
}

fun createQRCodeText(
    qrTextValue: String,
    charset: String,
    hintMap: MutableMap<EncodeHintType, ErrorCorrectionLevel>,
    smallestDimension: Int,
    smallestDimension1: Int,
    type: String
): Bitmap? {
    return try {
        val matrix = if (type == "QR") {
            MultiFormatWriter().encode(
                String(qrTextValue.toByteArray(charset(charset)), charset(charset)),
                BarcodeFormat.QR_CODE, smallestDimension, smallestDimension1, hintMap
            )
        } else {
            MultiFormatWriter().encode(
                String(qrTextValue.toByteArray(charset(charset)), charset(charset)),
                BarcodeFormat.CODE_128, smallestDimension, smallestDimension1, hintMap
            )
        }

        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        // All are 0, or black, by default
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (matrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        qrBitmap = bitmap
        bitmap
    } catch (er: java.lang.Exception) {
        er.message?.let { Log.e("QrGenerate", it) }
        null
    }
}*/

var myRepeatedText: String? = null

