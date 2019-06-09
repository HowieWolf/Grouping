package com.howie.grouping.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.howie.grouping.example.module1.business.Module1MainActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        btnGoM1.setOnClickListener {
            val intent = Intent(this, Module1MainActivity::class.java)
            startActivity(intent)
        }
    }
}
