package com.example.kushpatel_0859776_androidassignment6

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val name = findViewById<EditText>(R.id.editText)
        val button = findViewById<Button>(R.id.button)
        val result = findViewById<TextView>(R.id.finalResult)

        button.setOnClickListener {
            val finalResult = name.text.toString()
            result.text = "Hello, $finalResult"
        }
    }
}