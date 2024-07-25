package com.wix.detoxbutler

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.wix.detoxbutler.api.DetoxButlerApi
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


private const val DETOX_BUTLER_APP_PACKAGE = "com.wix.detoxbutler"
private const val DETOX_BUTLER_SERVICE_NAME = "com.wix.detoxbutler.DetoxButlerService"

object DetoxButler {

    private val componentName = ComponentName(
        DETOX_BUTLER_APP_PACKAGE, DETOX_BUTLER_SERVICE_NAME
    )

    private var serviceStarted = CountDownLatch(1)

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Timber.d("Service connected")
            serviceStarted.countDown()
            val api = DetoxButlerApi.Stub.asInterface(service)
            Timber.d("DetoxButlerService enabled: ${api.isDetoxButlerServiceEnabled()}")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.d("Service disconnected")
        }
    }

    fun setup(context: Context) {
        Timber.d("DetoxButler Setup")
        val intent = Intent()
        intent.setComponent(componentName)

        context.bindService(
            intent, serviceConnection, Context.BIND_AUTO_CREATE
        )
        try {
            if (!serviceStarted.await(15, TimeUnit.SECONDS)) {
                Timber.e("Timeout while trying to start DetoxButlerService. Did you install the DetoxButler app?")
            }
        } catch (e: InterruptedException) {
            throw IllegalStateException("Interrupted while trying to start ButlerService", e)
        }
    }

    fun teardown(context: Context) {
        Timber.d("DetoxButler teardown")
        val intent = Intent()
        intent.setComponent(
            componentName
        )

        context.unbindService(serviceConnection)
        serviceStarted = CountDownLatch(1)
    }
}