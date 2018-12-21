package org.meetsl.snetbus;

import android.util.Log;

/**
 * Created by shilong
 * 2018/12/19.
 */
public class NetBusTest {

    public void run() {
        NetBus.Companion.getDefault().register(this);
    }

    @NetSubscribe(netMode = NetMode.CELLULAR, threadMode = ThreadMode.MAIN, priority = 1)
    public void onEvent() {
        Log.i("Callback_Network", "NetBusTest ---- 网络变化了");
    }

    public void stop() {
        NetBus.Companion.getDefault().unregister(this);
    }
}
