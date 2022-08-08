package com.example.cubeinone

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.example.cubeinone.MainActivity.Constants.FILE_FORMAT
import com.example.cubeinone.MainActivity.Constants.REQUEST_CODE_PERMISSIONS
import com.example.cubeinone.MainActivity.Constants.REQUIRED_PERMISSIONS
import com.example.cubeinone.MainActivity.Constants.TAG
import com.example.cubeinone.databinding.ActivityMainBinding
import java.lang.Exception
import java.security.Permission
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity(){
    /* one binding object that has references for all views with id */
    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    //Constant values used for permissions
    object Constants{
        const val TAG = "cameraX"
        const val FILE_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //allows use of view binding to access views
        binding = ActivityMainBinding.inflate(layoutInflater)
        /*
        instead of passing the resource ID of the layout, R.layout.activity_main, this specifies the root of the hierarchy of views in your app

        //Old way with findViewById()
            val myButton: Button = findViewById(R.id.my_button)
            myButton.text = "A button"

        //Better way with view binding
            val myButton: Button = binding.myButton
            myButton.text = "A button"

        //Best way with view binding and no extra variable
            binding.myButton.text = "A button"

        */
        setContentView(binding.root)

        //Checks for user permissions status, will request permission if not yet granted
        if(allPermissionsGranted()){
            Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,REQUEST_CODE_PERMISSIONS)
        }

        binding.viewFinder.setOnTouchListener{v: View, m: MotionEvent ->
            handleTouch(m)
            true
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    //checks if a particular permission has been granted
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
    }

    /*
    *
    * */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) { //checks if request code is correct
            if (allPermissionsGranted()) { //starts the camera if permissions are granted
                startCamera()
            } else { //notifies user permissions weren't given
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    //lets user preview what they're capturing
    private fun startCamera() {
        //binds lifecycle of cameras to lifecycle owner
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //initialize preview object
            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            //creates video capture use case
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            //back camera is selected by default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            //bind use cases to camera
            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)
            }catch(exc: Exception){
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun captureVideo(){
        //checks if VideoCapture use case has been created
        val videoCapture = this.videoCapture ?: return

        //disables UI until request action completed by CameraX, re-enabled in VideoRecordListener
        binding.layoutMainActivity.isEnabled = false

        val curRecording = recording
        //if recording in progress, stop and release current recording
        if(curRecording != null){
            curRecording.stop()
            recording = null
            return
        }

        //create and start new recording
        val name = SimpleDateFormat(FILE_FORMAT, Locale.US).format(System.currentTimeMillis())

        val contentValues = ContentValues().apply{
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).setContentValues(contentValues).build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            return
        }
        recording = videoCapture.output.prepareRecording(this, mediaStoreOutputOptions).withAudioEnabled().apply{
            if(PermissionChecker.checkSelfPermission(this@MainActivity,Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED){
                withAudioEnabled()
            }
        }
        .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
            when(recordEvent) {
                is VideoRecordEvent.Finalize -> {
                    if (!recordEvent.hasError()) {
                        val msg = "Video capture succeeded: " +
                                "${recordEvent.outputResults.outputUri}"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                            .show()
                        Log.d(TAG, msg)
                    } else {
                        recording?.close()
                        recording = null
                        Log.e(TAG, "Video capture ends with error: " +
                                "${recordEvent.error}")
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun handleTouch(m: MotionEvent){
        when(m.actionMasked){
            MotionEvent.ACTION_UP -> captureVideo() //detects when touch is released

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    /*
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("seconds",seconds)
        outState.putBoolean("running",running)
        outState.putBoolean("wasRunning",wasRunning)
    }

    override fun onPause(){
        super.onPause()
        wasRunning = running
        running = false
    }

    val layout = findViewById<ConstraintLayout>(R.id.layoutMainActivity)

        layout.setOnTouchListener{ v: View, m: MotionEvent ->
            //handleTouch(m)
            true
        }

        if(savedInstanceState != null){
            seconds = savedInstanceState.getInt("seconds")
            running = savedInstanceState.getBoolean("running")
            wasRunning = savedInstanceState.getBoolean("wasRunning")
        }
        //runTimer()

    private fun runTimer(){
        val countTime: TextView = findViewById(R.id.timer)

        Handler(Looper.getMainLooper()).postDelayed({
            fun run(){
                var hours = seconds / 3600
                var minutes = (seconds % 3600) / 60
                var secs = seconds % 60

                var time = String.format(Locale.getDefault(),"%d:%02d:%02d", hours, minutes, secs)

                countTime.text = time

                if(running){
                    seconds++
                }
            }
        })

    }* */

    /*
    private fun handleTouch(m: MotionEvent){
        when(m.actionMasked){
            MotionEvent.ACTION_UP -> timer() //detects when touch on layout is released

        }
    }


    private fun timer() {
        val countTime: TextView = findViewById(R.id.timer)
        object: CountDownTimer(Long.MAX_VALUE,100){
            override fun onTick(millisUntilFinished: Long) {
                countTime.text = counter.toString()
                counter++
            }
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                countTime.text = "Finished"
            }
        }.start()
    }
    * */



}

