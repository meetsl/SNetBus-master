package org.meetsl.snetbus_master

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by meetsl
 *  2018/12/21.
 */
class TestPageAdapter(private val fragments: List<Fragment>, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}