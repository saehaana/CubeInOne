package com.example.cubeinone

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    var counter = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layout = findViewById<ConstraintLayout>(R.id.layoutMainActivity)

        layout.setOnTouchListener{ v: View, m: MotionEvent ->
            handleTouch(m)
            true
        }

    }

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

    

}