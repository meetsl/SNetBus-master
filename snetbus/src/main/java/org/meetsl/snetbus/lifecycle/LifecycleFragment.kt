package org.meetsl.snetbus.lifecycle

import android.support.v4.app.Fragment

/**
 * Created by meetsl
 *  2018/12/21.
 *
 *   这是一个空白的 Fragment，将其添加在 FragmentActivity 或者 Fragment 中，同步生命周期
 */
class LifecycleFragment : Fragment() {
    private val mListeners: MutableList<LifecycleListener> = mutableListOf()

    fun addLifecycleListener(listener: LifecycleListener) {
        mListeners.add(listener)
    }

    override fun onStart() {
        super.onStart()
        mListeners.forEach {
            it.onStart()
        }
    }

    override fun onStop() {
        super.onStop()
        mListeners.forEach {
            it.onStop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mListeners.forEach {
            it.onDestroy()
        }
    }
}