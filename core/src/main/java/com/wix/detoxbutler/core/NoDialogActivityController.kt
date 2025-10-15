package com.wix.detoxbutler.core

import android.app.IActivityController
import android.content.Intent
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.util.Log
import timber.log.Timber

/**
 * An implementation [IActivityController] that prevents the system dialogs like anr and crashes
 * from being displayed.
 *
 * The implementation is taken from: [https://github.com/linkedin/test-butler/blob/main/test-butler-app-core/src/main/java/com/linkedin/android/testbutler/NoDialogActivityController.java]
 */
class NoDialogActivityController : IActivityController.Stub() {
    override fun activityStarting(intent: Intent?, pkg: String?): Boolean {
        Timber.d("activityStarting: $intent")

        // Return true to allow the activity to start
        return true
    }

    override fun activityResuming(pkg: String?): Boolean {
        Timber.d("activityResuming: $pkg")

        // Return true to allow the activity to resume
        return true
    }

    override fun appCrashed(
        processName: String?,
        pid: Int,
        shortMsg: String?,
        longMsg: String?,
        timeMillis: Long,
        stackTrace: String?
    ): Boolean {
        Timber.i("appCrashed: $processName, $pid, $shortMsg, $longMsg, $timeMillis, $stackTrace")

        // Return false to prevent the crash dialog from being displayed
        return false
    }

    override fun appEarlyNotResponding(processName: String?, pid: Int, annotation: String?): Int {
        Timber.i("appEarlyNotResponding: $processName, $pid, $annotation")

        // Return 0 to continue waiting for the app to respond. The ANR dialog will be prevented
        // in #appNotResponding callback
        return 0
    }

    override fun appNotResponding(processName: String?, pid: Int, processStats: String?): Int {
        Timber.i("appNotResponding: $processName, $pid, $processStats")

        // Return -1 to kill the app process immediately and prevent the ANR dialog from being displayed
        return -1
    }

    override fun systemNotResponding(msg: String?): Int {
        Timber.i("systemNotResponding: $msg")

        // Return -1 to kill the app process immediately
        return -1
    }

    companion object {
        fun install() {
            setActivityController(NoDialogActivityController())
        }

        fun uninstall() {
            setActivityController(null)
        }

        private fun setActivityController(activityController: IActivityController?) {
            try {
                val amClass = Class.forName("android.app.ActivityManager")
                val getService = amClass.getMethod("getService")
                val am = getService.invoke(null) as IInterface
                val binder = am.asBinder()

                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                data.writeInterfaceToken("android.app.IActivityManager")
                data.writeStrongBinder(activityController?.asBinder())
                data.writeInt(0) // imAMonkey = true

                // TRANSACTION_setActivityController: check below
                binder.transact(51, data, reply, 0)

                reply.recycle()
                data.recycle()
                Timber.i("Binder controller installed successfully")
            } catch (t: Throwable) {
                Timber.i(t, "Binder install failed")
            }
        }


    }
}