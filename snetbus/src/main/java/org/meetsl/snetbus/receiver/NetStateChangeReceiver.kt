package org.meetsl.snetbus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.Log

/**
 * Created by shilong
 *  2018/12/24.
 */
class NetStateChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("Callback_Network", "NetStateChangeReceiver ------- 收到广播")
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent?.action && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //获取联网状态的NetworkInfo对象
            val info = intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.state && info.isAvailable) {
                    if (info.type == ConnectivityManager.TYPE_WIFI || info.type == ConnectivityManager.TYPE_MOBILE) {
                        Log.i("Callback_Network", "NetStateChangeReceiver ${getConnectionType(info.type)} 连上")
                    }
                } else {
                    Log.i("Callback_Network", "NetStateChangeReceiver ${getConnectionType(info.type)} 断开")
                }
            }
        }
    }

    private fun getConnectionType(type: Int): String {
        var connType = ""
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "3G网络数据"
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI网络"
        }
        return connType
    }
}