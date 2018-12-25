package org.meetsl.snetbus.lifecycle

import org.meetsl.snetbus.NetBus

/**
 * Created by meetsl
 *  2018/12/21.
 */
open class LifecycleAdapter(private val subscriber: Any) : LifecycleListener {
    override fun onStart() {

    }

    override fun onStop() {

    }

    override fun onDestroy() {
        NetBus.getDefault().unregister(subscriber)
    }
}