package org.meetsl.snetbus

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by shilong
 *  2018/12/21.
 */
class OneFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetBus.getDefault().register(this)
        return inflater.inflate(R.layout.fragment_one, container, false)
    }

    @NetSubscribe(netMode = NetMode.WIFI)
    fun onNetEvent() {
        Log.i("Callback_Network", "${this.javaClass} 网络变化了")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NetBus.getDefault().unregister(this)
    }
}