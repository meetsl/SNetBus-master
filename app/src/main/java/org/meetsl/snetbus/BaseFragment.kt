package org.meetsl.snetbus

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by shilong
 *  2018/12/21.
 */
abstract class BaseFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetBus.getDefault().register(this)
        return inflater.inflate(getLayoutId(), container, false)
    }

    abstract fun getLayoutId(): Int
}