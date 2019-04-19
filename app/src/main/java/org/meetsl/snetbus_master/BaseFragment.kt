package org.meetsl.snetbus_master

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.meetsl.snetbus.NetBus

/**
 * Created by meetsl
 *  2018/12/21.
 */
abstract class BaseFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetBus.getDefault().register(this)
        return inflater.inflate(getLayoutId(), container, false)
    }

    abstract fun getLayoutId(): Int
}