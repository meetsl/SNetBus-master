package org.meetsl.snetbus

import android.app.Application

/**
 * Created by shilong
 *  2018/11/26.
 */
open class SApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NetBus.init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        NetBus.terminate()
    }
}