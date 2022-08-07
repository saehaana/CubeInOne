package com.example.cubeinone

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat.postDelayed
import com.example.cubeinone.MainActivity.Constants.REQUEST_CODE_PERMISSIONS
import com.example.cubeinone.MainActivity.Constants.REQUIRED_PERMISSIONS
import com.example.cubeinone.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(){
    var counter = 0
    private var seconds = 0
    private var running : Boolean = true
    private var wasRunning : Boolean = true

    /* one binding object that has references for all views with id */
    private lateinit var binding: ActivityMainBinding

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

        if(allPermissionsGranted()){
            Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,REQUEST_CODE_PERMISSIONS)
        }

    }
    object Constants{
        const val TAG = "cameraX"
        const val FILE_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        TODO("Not yet implemented")
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

