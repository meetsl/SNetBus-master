package org.meetsl.snetbus_master

import android.app.Application
import org.meetsl.snetbus.NetBus

/**
 * Created by meetsl
 *  2018/11/26.
 */
open class SApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //初始化 NetBus
        NetBus.init(this)
        //全局设置公共布局
        NetBus.initNetView(loadResId = R.layout.layout_loading_view, netErrorResId = R.layout.layout_net_error_view)
    }

    override fun onTerminate() {
        super.onTerminate()
        NetBus.terminate()
    }
}