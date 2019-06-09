package com.howie.grouping.example

import android.app.Application

class ExampleApplication:Application() {

    /**
     * 当前版本你必须重写 [onCreate] 方法
     * 之后的版本会实现允许不重写
     */
    override fun onCreate() {
        super.onCreate()
    }
}