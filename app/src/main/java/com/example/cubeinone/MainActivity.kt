package com.example.cubeinone

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layout = findViewById<ConstraintLayout>(R.id.layoutMainActivity)

        layout.setOnTouchListener{v: View, m: MotionEvent ->
            handleTouch(m)
            true
        }

    }

    private fun handleTouch(m: MotionEvent){
        when(m.actionMasked){
            MotionEvent.ACTION_DOWN -> Log.i("TouchEvents","Action Down")
            MotionEvent.ACTION_UP -> Log.i("TouchEvents","Action Up")
        }
    }
}