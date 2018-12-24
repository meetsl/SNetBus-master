package org.meetsl.snetbus_master

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.meetsl.snetbus.NetBus
import org.meetsl.snetbus.NetMode
import org.meetsl.snetbus.NetSubscribe

/**
 * Created by shilong
 *  2018/12/21.
 */
class PageFragment : Fragment() {
    private var mTvName: TextView? = null
    private var name = ""

    companion object {
        fun newInstance(name: String): PageFragment {
            val pageFragment = PageFragment()
            pageFragment.name = name
            return pageFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_page, container, false)
        mTvName = rootView.findViewById(R.id.tv_name)

        NetBus.getDefault().register(this)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTvName?.text = name
    }

    @NetSubscribe(netMode = NetMode.WIFI)
    fun onNetEvent() {
        Log.i("Callback_Network", "${this.javaClass} $name 网络变化了")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NetBus.getDefault().unregister(this)
    }
}