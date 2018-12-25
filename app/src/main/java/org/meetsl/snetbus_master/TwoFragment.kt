package org.meetsl.snetbus_master

import android.util.Log
import org.meetsl.snetbus.NetMode
import org.meetsl.snetbus.NetSubscribe

/**
 * Created by meetsl
 *  2018/12/21.
 */
class TwoFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_two
    }

    @NetSubscribe(netMode = NetMode.WIFI)
    fun onNetEvent(isAvailable: Boolean) {
        Log.i("Callback_Network", "${this.javaClass} 网络变化了")
    }
}