package com.example.cubeinone

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        val pointerCount = m.pointerCount //identifies pointers active on the view

        for(i in 0 until pointerCount){
            val x = m.getX(i)
            val y = m.getY(i)
            val id = m.getPointerId(i)
            val action = m.actionMasked
            val actionIndex = m.actionIndex
            val actionString: String

            when(action){
                MotionEvent.ACTION_BUTTON_RELEASE -> actionString = "BTN RELEASE"
                else -> actionString = ""
            }


        }
    }
}