package org.meetsl.snetbus.lifecycle

/**
 * Created by meetsl
 *  2018/12/21.
 */
interface LifecycleListener {
    fun onStart()
    fun onStop()
    fun onDestroy()
}