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

private const val HELP_LOG_MESSAGE = "Did you install the DetoxButler app? Did you add <queries> to your AndroidManifest.xml?"

@Suppress("unused")
object DetoxButler {
    private val componentName = ComponentName(
        DETOX_BUTLER_APP_PACKAGE, DETOX_BUTLER_SERVICE_NAME
    )

    private var serviceStarted = CountDownLatch(1)

    private var detoxServiceApi: DetoxButlerApi? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Timber.d("Service connected")
            serviceStarted.countDown()
            detoxServiceApi = DetoxButlerApi.Stub.asInterface(service)
            Timber.d("DetoxButlerService enabled: ${detoxServiceApi?.isDetoxButlerServiceEnabled()}")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.d("Service disconnected")
        }
    }

    @JvmStatic
    fun setup(context: Context) {
        Timber.d("DetoxButler Setup")
        val intent = Intent()
        intent.setComponent(componentName)

        val bindResult = context.bindService(
            intent, serviceConnection, Context.BIND_AUTO_CREATE
        )

        if (!bindResult) {
            Timber.e("Failed to bind to DetoxButlerService. $HELP_LOG_MESSAGE")
            return
        }

        try {
            if (!serviceStarted.await(15, TimeUnit.SECONDS)) {
                Timber.e("Timeout while trying to start DetoxButlerService. $HELP_LOG_MESSAGE")
            }
        } catch (e: InterruptedException) {
            throw IllegalStateException("Interrupted while trying to start ButlerService", e)
        }
    }

    @JvmStatic
    fun teardown(context: Context) {
        Timber.d("DetoxButler teardown")
        val intent = Intent()
        intent.setComponent(
            componentName
        )

        context.unbindService(serviceConnection)
        serviceStarted = CountDownLatch(1)
    }

    @JvmStatic
    fun isDetoxButlerServiceInstalled(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(DETOX_BUTLER_APP_PACKAGE, 0)
            true
        } catch (e: Exception) {
            Timber.e("DetoxButler app is not installed. $HELP_LOG_MESSAGE")
            false
        }
    }

    @JvmStatic
    fun isDetoxButlerServiceEnabled(): Boolean {
        return detoxServiceApi?.isDetoxButlerServiceEnabled() ?: false
    }

    @JvmStatic
    @Throws(IllegalStateException::class)
    fun tryToWaitForDetoxButlerServiceToBeEnabled(timeoutSeconds: Long = 15): Boolean {
        try {
            if (serviceStarted.await(timeoutSeconds, TimeUnit.SECONDS)) {
                return isDetoxButlerServiceEnabled()
            }

            Timber.e("Timeout while trying to get service status. Did you install the DetoxButler app?")
        } catch (e: InterruptedException) {
            throw IllegalStateException("Interrupted while trying to get service status", e)
        }

        return false
    }
}