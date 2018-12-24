package org.meetsl.snetbus_master

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import org.meetsl.snetbus.NetBus
import org.meetsl.snetbus.NetMode
import org.meetsl.snetbus.NetSubscribe
import org.meetsl.snetbus.ThreadMode
import org.meetsl.snetbus_master.lifecycle.test.TestLifecycleActivity

class MainActivity : AppCompatActivity() {

    private val netBusTest = NetBusTest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NetBus.getDefault().register(this)
        netBusTest.run()
    }

    @NetSubscribe(netMode = NetMode.WIFI, threadMode = ThreadMode.POSTING, priority = 1)
    fun onEvent() {
        println("网络变化了")
        Log.i("Callback_Network", "MainActivity ---- 网络变化了")
    }

    override fun onDestroy() {
        netBusTest.stop()
        super.onDestroy()
    }

    fun pageToSecond(view: View) {
        startActivity(Intent(this, SecondActivity::class.java))
    }

    fun pageToThird(view: View) {
        startActivity(Intent(this, ThirdActivity::class.java))
    }

    fun pageToLifecycle(view: View) {
        startActivity(Intent(this, TestLifecycleActivity::class.java))
    }
}
