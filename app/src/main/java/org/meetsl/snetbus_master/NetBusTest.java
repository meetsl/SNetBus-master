package org.meetsl.snetbus_master;

import android.util.Log;

import org.meetsl.snetbus.NetBus;
import org.meetsl.snetbus.NetMode;
import org.meetsl.snetbus.NetSubscribe;
import org.meetsl.snetbus.ThreadMode;

/**
 * Created by meetsl
 * 2018/12/19.
 */
public class NetBusTest {

    /**
     * 需要在适当的时机，比如类对象创建的时候就进行注册
     */
    public void run() {
        NetBus.Companion.getDefault().register(this);
    }

    /**
     * 接收网络状态变化，这里监听的是移动数据网络
     *
     * @param isAvailable true 当前 NetMode 下网络可用；false 无网络状态，与 NetMode 无关
     */
    @NetSubscribe(netMode = NetMode.CELLULAR, threadMode = ThreadMode.MAIN, priority = 1)
    public void onEvent(boolean isAvailable) {
        Log.i("Callback_Network", "NetBusTest ---- 网络变化了");
    }

    /**
     * 在类对象不再被使用或者销毁的时候，进行解注册
     */
    public void stop() {
        NetBus.Companion.getDefault().unregister(this);
    }
}
