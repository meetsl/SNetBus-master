package org.meetsl.snetbus_master

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_third.*
import org.meetsl.snetbus.NetBus
import org.meetsl.snetbus.NetMode
import org.meetsl.snetbus.NetSubscribe

/**
 * Created by shilong
 *  2018/12/21.
 */
class ThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        NetBus.getDefault().register(this)
        val fragments = mutableListOf<Fragment>()
        for (i in 0 until 3) {
            fragments.add(PageFragment.newInstance("PageFragment $i"))
        }
        vp_page.adapter = TestPageAdapter(fragments, supportFragmentManager)
    }

    @NetSubscribe(netMode = NetMode.WIFI)
    fun onNetEvent() {
        Log.i("Callback_Network", "${this.javaClass} 网络变化了")
    }

    override fun onDestroy() {
        super.onDestroy()
        NetBus.getDefault().unregister(this)
    }
}