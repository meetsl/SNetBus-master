package org.meetsl.snetbus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import org.meetsl.snetbus.NetBus
import org.meetsl.snetbus.NetMode

/**
 * Created by meetsl
 *  2018/12/24.
 *
 *  对Android 5.0 以及以下版本的兼容,采用静态广播的方式监听网络状态
 */
@Suppress("DEPRECATION")
class NetStateChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent?.action && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //获取联网状态的NetworkInfo对象
            val info = intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.state && info.isAvailable) {
                    if (info.type == ConnectivityManager.TYPE_WIFI) {
                        NetBus.getDefault().setNetMode(NetMode.WIFI)
                    } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                        NetBus.getDefault().setNetMode(NetMode.CELLULAR)
                    }
                } else {
                    NetBus.getDefault().setNetMode(NetMode.UNAVAILABLE_NET)
                }
            }
        }
    }
}