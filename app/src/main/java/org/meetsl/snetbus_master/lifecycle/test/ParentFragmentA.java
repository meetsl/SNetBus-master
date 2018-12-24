package org.meetsl.snetbus_master.lifecycle.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.meetsl.snetbus.NetMode;
import org.meetsl.snetbus.NetSubscribe;
import org.meetsl.snetbus_master.BaseFragment;
import org.meetsl.snetbus_master.R;

/**
 * @author meetsl
 * <p>
 * 测试 继承下的 Fragment 注册 以及 fragment 以 replace 的方式引入测试
 */
public class ParentFragmentA extends BaseFragment {

    @Override
    public int getLayoutId() {
        return R.layout.parent_fragment_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = view.findViewById(R.id.tv_name);
        textView.setText(R.string.a_content);
    }

    @NetSubscribe(netMode = NetMode.WIFI, priority = 100)
    public void onEvent() {
        Log.i("Callback_Network", getClass().getName() + "网络变化了");
    }

    @NetSubscribe(netMode = NetMode.CELLULAR)
    public void secondEvent() {
        Log.i("Callback_Network", getClass().getName() + "secondEvent 网络变化了");
    }
}