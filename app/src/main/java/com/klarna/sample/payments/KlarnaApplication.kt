package com.klarna.sample.payments

import android.app.Application
import com.klarna.mobile.sdk.payments.KlarnaPaymentsSDK

class KlarnaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KlarnaPaymentsSDK.initialize(this)
    }

}