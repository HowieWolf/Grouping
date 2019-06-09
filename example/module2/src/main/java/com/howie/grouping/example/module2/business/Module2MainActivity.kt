package com.howie.grouping.example.module2.business

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.howie.grouping.core.Grouping
import com.howie.grouping.example.module2.R

class Module2MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m2_activity_main)
        Grouping.getInstance()
    }
}