package org.meetsl.snetbus_master

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import org.meetsl.snetbus.NetBus

/**
 * Created by meetsl
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