package com.howie.grouping.example.module2.api.applike

import com.howie.grouping.core.Grouping
import com.howie.grouping.core.IAppLike
import com.howie.grouping.example.module2.api.service.M2RouterService
import com.howie.grouping.example.module2.business.serviceimpl.M2RouterServiceImpl

class Module2Applike: IAppLike {
    override fun onCreate() {
       Grouping.getInstance().addService(M2RouterService::class.java, M2RouterServiceImpl())
    }

    override fun onDestroy() {

    }
}