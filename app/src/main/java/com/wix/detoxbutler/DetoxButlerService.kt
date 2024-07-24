package com.wix.detoxbutler

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.wix.detoxbutler.core.NoDialogActivityController
import timber.log.Timber

class DetoxButlerService : Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): DetoxButlerService = this@DetoxButlerService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("DetoxButlerService created")
        NoDialogActivityController.install()
    }

    override fun onDestroy() {
        Timber.d("DetoxButlerService destroyed")
        NoDialogActivityController.uninstall()
        super.onDestroy()
    }
}