package org.meetsl.snetbus_master.lifecycle.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.meetsl.snetbus.NetBus
import org.meetsl.snetbus.NetMode
import org.meetsl.snetbus.NetSubscribe
import org.meetsl.snetbus_master.R

/**
 * Created by meetsl
 *  2018/12/21.
 */
class TestLifecycleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_lifecycle)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_main, ParentFragmentA())
        transaction.commit()

        NetBus.getDefault().register(this)
    }

    @NetSubscribe(netMode = NetMode.WIFI)
    fun onEvent(isAvailable: Boolean) {
        Log.i("Callback_Network", "${this.javaClass} 网络变化了")
    }
}