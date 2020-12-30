package com.example.ponyexpressmanager

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "727bef7ea8c33cf245841de714e5e919")
    }
}