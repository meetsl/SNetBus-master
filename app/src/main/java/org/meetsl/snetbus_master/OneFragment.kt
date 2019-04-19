package org.meetsl.snetbus_master

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.meetsl.snetbus.NetBus
import org.meetsl.snetbus.NetMode
import org.meetsl.snetbus.NetSubscribe

/**
 * Created by meetsl
 *  2018/12/21.
 */
class OneFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetBus.getDefault().register(this)
        return inflater.inflate(R.layout.fragment_one, container, false)
    }

    @NetSubscribe(netMode = NetMode.WIFI)
    fun onNetEvent(isAvailable: Boolean) {
        Log.i("Callback_Network", "${this.javaClass} 网络变化了")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NetBus.getDefault().unregister(this)
    }
}