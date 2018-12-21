package org.meetsl.snetbus

import android.util.Log

/**
 * Created by shilong
 *  2018/12/21.
 */
class TwoFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_two
    }

    @NetSubscribe(netMode = NetMode.WIFI)
    fun onNetEvent() {
        Log.i("Callback_Network", "${this.javaClass} 网络变化了")
    }
}