package org.meetsl.snetbus_master

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import org.meetsl.snetbus.NetMode
import org.meetsl.snetbus.NetSubscribe

/**
 * Created by meetsl
 *  2018/12/21.
 *
 *  测试 继承下的 Activity 注册 以及 fragment 以 replace 的方式引入测试
 */
class SecondActivity : BaseActivity() {
    private lateinit var oneFragment: Fragment
    private lateinit var twoFragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)


        oneFragment = OneFragment()
        twoFragment = TwoFragment()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_main, oneFragment, "OneFragment")
        transaction.add(R.id.fl_main, twoFragment, "TwoFragment")
        transaction.hide(oneFragment)
        transaction.show(twoFragment)
        transaction.commit()
    }

    fun pageToOne(view: View) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.show(oneFragment)
        transaction.hide(twoFragment)
        transaction.commit()
    }

    fun pageToTwo(view: View) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.show(twoFragment)
        transaction.hide(oneFragment)
        transaction.commit()
    }

    @NetSubscribe(netMode = NetMode.WIFI)
    fun onNetEvent(isAvailable: Boolean) {
        Log.i("Callback_Network", "${this.javaClass} 网络变化了")
    }
}