package org.meetsl.snetbus

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by shilong
 *  2018/12/21.
 */
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        NetBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}