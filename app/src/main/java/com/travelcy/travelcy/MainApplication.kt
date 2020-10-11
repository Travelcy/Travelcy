package com.travelcy.travelcy

import android.app.Application
import com.bugsnag.android.Bugsnag

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Bugsnag.start(this)
    }
}