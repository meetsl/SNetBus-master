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
import org.meetsl.snetbus.widget.PageStateLayout
import org.meetsl.snetbus_master.lifecycle.test.TestLifecycleActivity

class MainActivity : AppCompatActivity() {

    private val netBusTest = NetBusTest()

    private lateinit var stateLayout: PageStateLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stateLayout = PageStateLayout(this)
        //设置正常显示视图
        stateLayout.setNormalView(R.layout.activity_main)
        //设置该 Page 的 空视图
        stateLayout.setEmptyView(R.layout.layout_empty_default)
        //设置该 Page 的加载视图
        stateLayout.setLoadingView(R.layout.layout_loading_view)
        //设置该 Page 的网络错误视图
        stateLayout.setNetErrorView(R.layout.layout_net_error_view)
        //显示
        setContentView(stateLayout)
        stateLayout.showNormalView()
        NetBus.getDefault().register(this)
        netBusTest.run()
    }

    @NetSubscribe(netMode = NetMode.WIFI, threadMode = ThreadMode.POSTING, priority = 1)
    fun onEvent(isAvailable: Boolean) {
        println("网络变化了")
        if (!isAvailable)
            stateLayout.showNetErrorView()
        else
            stateLayout.showNormalView()
        Log.i("Callback_Network", "MainActivity ----$isAvailable 网络变化了")
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
