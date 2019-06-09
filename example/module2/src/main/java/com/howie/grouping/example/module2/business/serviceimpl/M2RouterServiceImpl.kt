package com.howie.grouping.example.module2.business.serviceimpl

import android.content.Context
import android.content.Intent
import com.howie.grouping.example.module2.api.service.M2RouterService
import com.howie.grouping.example.module2.business.Module2MainActivity

class M2RouterServiceImpl : M2RouterService {
    override fun launchM2MainActivity(context: Context) {
        val intent = Intent(context, Module2MainActivity::class.java)
        context.startActivity(intent)
    }
}