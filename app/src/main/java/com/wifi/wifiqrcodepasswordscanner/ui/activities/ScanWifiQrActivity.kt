package com.wifi.wifiqrcodepasswordscanner.ui.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.blikoon.qrcodescanner.camera.CameraManager
import com.blikoon.qrcodescanner.decode.InactivityTimer
import com.google.zxing.Result
import com.wifi.wifiqrcodepasswordscanner.R
import com.wifi.wifiqrcodepasswordscanner.databinding.ActivityScanWifiqrActivityBinding
import com.wifi.wifiqrcodepasswordscanner.extensions.mScannedResultString
import com.wifi.wifiqrcodepasswordscanner.extensions.setSafeOnClickListener
import com.wifi.wifiqrcodepasswordscanner.models.wifiScanQr.CaptureActivityHandler
import com.wifi.wifiqrcodepasswordscanner.models.wifiScanQr.DecodeManager
import me.dm7.barcodescanner.core.CameraUtils
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ScanWifiQrActivity : AppCompatActivity(), SurfaceHolder.Callback {
    private lateinit var binding: ActivityScanWifiqrActivityBinding
    private var mHasSurface = false
    private var mInactivityTimer: InactivityTimer? = null
    private val mDecodeManager = DecodeManager()
    private var mMediaPlayer: MediaPlayer? = null
    private var mPlayBeep = false
    private var mVibrate = false
    private var mNeedFlashLightOpen = true
    private var mQrCodeExecutor: Executor? = null
    private var mHandler: Handler? = null
    private var mCamera: Camera? = null
    private var constScannerView: ConstraintLayout? = null
    private var path: String? = null
    private val GOT_RESULT = "got_qr_scan_result"
    private val ERROR_DECODING_IMAGE = "error_decoding_image"
    var resultData: String? = null
    private val mBeepListener = MediaPlayer.OnCompletionListener { mediaPlayer -> mediaPlayer.seekTo(0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanWifiqrActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initData()
        setClicks()
    }

    private fun initViews() {
        constScannerView = findViewById(R.id.scanerlayout)
        mHasSurface = false
        mCamera = CameraUtils.getCameraInstance()
    }

    private fun initData() {
        CameraManager.init(this)
        mInactivityTimer = InactivityTimer(this)
        mQrCodeExecutor = Executors.newSingleThreadExecutor()
        mHandler = Handler(Looper.getMainLooper())

        // Load the vibration and beep settings from SharedPreferences
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        mVibrate = sharedPreferences.getBoolean("vibrate_on_scan", false)
        mPlayBeep = sharedPreferences.getBoolean("beep_on_scan", false)

        initBeepSound()  // Initialize the beep sound
    }

    private fun setClicks() {
        binding.qrAutofocus.setSafeOnClickListener {
            restartPreview()
        }

        binding.imgFlashLight.setOnClickListener {
            if (mNeedFlashLightOpen) {
                turnFlashLightOn()
            } else {
                turnFlashLightOff()
            }
        }
    }

    private fun turnFlashLightOff() {
        mNeedFlashLightOpen = true
        binding.imgFlashLight.setBackgroundResource(R.drawable.ic_flash_ic)

        try {
            // Check if the camera is initialized before turning off the flashlight
            if (CameraManager.get() != null) {
                CameraManager.get().setFlashLight(false)
            } else {
                Log.e("ScanWifiQrActivity", "CameraManager is not initialized.")
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
            Log.e("ScanWifiQrActivity", "Failed to turn off flashlight: ${e.message}")
        }
    }

    private fun turnFlashLightOn() {
        mNeedFlashLightOpen = false
        binding.imgFlashLight.setBackgroundResource(R.drawable.ic_flash_ic)
        try {
            CameraManager.get().setFlashLight(true)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        val surfaceHolder = binding.mSurfaceView.holder
        turnFlashLightOff()
        if (mHasSurface) {
            initCamera(surfaceHolder)
            restartPreview()
        } else {
            surfaceHolder.addCallback(this)
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
        initBeepSound()
        restartPreview()
    }

    override fun onPause() {
        super.onPause()
        try {
            mCaptureActivityHandler?.quitSynchronously()
            mCaptureActivityHandler = null
            CameraManager.get().closeDriver()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        mInactivityTimer?.shutdown()
        super.onDestroy()
    }

    fun handleDecode(result: Result?) {
        mInactivityTimer?.onActivity()
        playBeepSoundAndVibrate()
        if (result == null) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(this) { restartPreview() }
        } else {
            val resultString = result.text
            handleResult(resultString)
        }
    }

    private fun initCamera(surfaceHolder: SurfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder)
            findViewById<View>(R.id.qr_code_view_background).visibility = View.GONE
            if (mCaptureActivityHandler == null) {
                mCaptureActivityHandler = CaptureActivityHandler(this)
            }
        } catch (e: IOException) {
            Log.e("ScanWifiQrActivity", "IOException while opening camera driver: ${e.message}")
        } catch (re: RuntimeException) {
            re.printStackTrace()
            mDecodeManager.showPermissionDeniedDialog(this)
        }
    }

    private fun handleResult(resultString: String) {
        if (TextUtils.isEmpty(resultString)) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(this) { restartPreview() }
        } else {
            resultData = resultString
            val data = Intent()
            data.putExtra(GOT_RESULT, resultString)
            mScannedResultString = resultString
            startFun(resultString)
        }
    }

    private fun startFun(resultString: String) {
        val intent = Intent(this, ResultsActivity::class.java)
        startActivity(intent)
    }

    private fun restartPreview() {
        mCaptureActivityHandler?.restartPreviewAndDecode()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    private fun checkCameraHardWare(context: Context?): Boolean {
        val packageManager = context?.packageManager
        return packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA) == true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!mHasSurface) {
            mHasSurface = true
            initCamera(holder)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mHasSurface = false
    }

    val captureActivityHandler: Handler?
        get() = mCaptureActivityHandler

    private fun initBeepSound() {
        if (mPlayBeep && mMediaPlayer == null) {
            volumeControlStream = AudioManager.STREAM_MUSIC
            mMediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setOnCompletionListener(mBeepListener)
                val file = resources.openRawResourceFd(R.raw.beep)
                try {
                    setDataSource(file.fileDescriptor, file.startOffset, file.length)
                    file.close()
                    setVolume(0.1f, 0.1f)
                    prepare()
                } catch (ioe: IOException) {
                    mMediaPlayer = null
                    Log.e("ScanWifiQrActivity", "Failed to initialize beep sound: ${ioe.message}")
                }
            }
        }
    }

    private fun playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            try {
                mMediaPlayer?.start()
            } catch (e: Exception) {
                Log.e("ScanWifiQrActivity", "Error playing beep sound: ${e.message}")
            }
        }
        if (mVibrate) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VIBRATE_DURATION)
        }
    }

    private var mCaptureActivityHandler: CaptureActivityHandler? = null

    companion object {
        private const val VIBRATE_DURATION = 200L
    }
}
