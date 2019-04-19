package org.meetsl.snetbus.lifecycle

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Created by meetsl
 *  2018/12/21.
 */
class LifecycleRegister {
    private val LISTENER_FRAGMENT_TAG = "listener_fragment_tag"
    private var mListenerFragment: LifecycleFragment? = null
    private var mContext: Context? = null

    /**
     * Register fragment into FragmentListener
     *
     * @param fragment
     */
    fun registerFragment(fragment: Fragment) {
        mContext = fragment.activity
        if (mListenerFragment == null) {
            mListenerFragment = LifecycleFragment()
        }
        mListenerFragment!!.addLifecycleListener(LifecycleAdapter(fragment))
        // 由于Fragment的bug，必须将mChildFragmentManager的accessible设为true
        compatibleFragment(fragment)

        fragment.childFragmentManager
                .beginTransaction()
                .add(mListenerFragment!!, LISTENER_FRAGMENT_TAG)
                .commitAllowingStateLoss()
    }

    fun registerActivity(fragmentActivity: FragmentActivity) {
        val fragmentManager = fragmentActivity.supportFragmentManager
        if (mListenerFragment == null) {
            mListenerFragment = LifecycleFragment()
        }
        mListenerFragment!!.addLifecycleListener(LifecycleAdapter(fragmentActivity))

        fragmentManager.beginTransaction()
                .add(mListenerFragment!!, LISTENER_FRAGMENT_TAG)
                .commitAllowingStateLoss()
    }

    /**
     * For bug of Fragment in Android
     * https://issuetracker.google.com/issues/36963722
     *
     * @param fragment
     */
    private fun compatibleFragment(fragment: Fragment) {
        try {
            val childFragmentManager = Fragment::class.java.getDeclaredField("mChildFragmentManager")
            childFragmentManager.isAccessible = true
            childFragmentManager.set(fragment, null)
        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }

    }
}