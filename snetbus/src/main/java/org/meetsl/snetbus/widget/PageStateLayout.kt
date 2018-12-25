package org.meetsl.snetbus.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import org.meetsl.snetbus.NetBus

/**
 * Created by shilong
 *  2018/12/25.
 */
class PageStateLayout(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attributeSet, defStyle) {
    private val defNormalView: View by lazy { LayoutInflater.from(context).inflate(NetBus.normalResId, this, false) }
    private val defEmptyView: View by lazy { LayoutInflater.from(context).inflate(NetBus.emptyResId, this, false) }
    private val defLoadView: View by lazy { LayoutInflater.from(context).inflate(NetBus.loadResId, this, false) }
    private val defNetErrorView: View by lazy { LayoutInflater.from(context).inflate(NetBus.netErrorResId, this, false) }

    var customNormalView: View? = null
    var customEmptyView: View? = null
    var customLoadView: View? = null
    var customNetErrorView: View? = null

    private val EMPTY_VIEW_CHILD_POSITION: Int = 0
    private val LOAD_VIEW_CHILD_POSITION: Int = 1
    private val NORMAL_VIEW_CHILD_POSITION: Int = 2
    private val NET_ERROR_VIEW_CHILD_POSITION: Int = 3

    init {
        addView(customEmptyView ?: defEmptyView, EMPTY_VIEW_CHILD_POSITION)
        addView(customLoadView ?: defLoadView, LOAD_VIEW_CHILD_POSITION)
        addView(customNormalView ?: defNormalView, NORMAL_VIEW_CHILD_POSITION)
        addView(customNetErrorView ?: defNetErrorView, NET_ERROR_VIEW_CHILD_POSITION)
        showNormalView()
    }

    fun setNormalView(resId: Int) {
        customNormalView = LayoutInflater.from(context).inflate(resId, this, false)
        removeViewAt(NORMAL_VIEW_CHILD_POSITION)
        addView(customNormalView ?: defNormalView, NORMAL_VIEW_CHILD_POSITION)
    }

    fun setNormalView(view: View?) {
        customNormalView = view
        removeViewAt(NORMAL_VIEW_CHILD_POSITION)
        addView(customNormalView ?: defNormalView, NORMAL_VIEW_CHILD_POSITION)
    }

    fun setEmptyView(resId: Int) {
        customEmptyView = LayoutInflater.from(context).inflate(resId, this, false)
        removeViewAt(EMPTY_VIEW_CHILD_POSITION)
        addView(customEmptyView ?: defEmptyView, EMPTY_VIEW_CHILD_POSITION)
    }

    fun setEmptyView(view: View?) {
        customEmptyView = view
        removeViewAt(EMPTY_VIEW_CHILD_POSITION)
        addView(customEmptyView ?: defEmptyView, EMPTY_VIEW_CHILD_POSITION)
    }

    fun setLoadingView(resId: Int) {
        customLoadView = LayoutInflater.from(context).inflate(resId, this, false)
        removeViewAt(LOAD_VIEW_CHILD_POSITION)
        addView(customLoadView ?: defLoadView, LOAD_VIEW_CHILD_POSITION)
    }

    fun setLoadingView(view: View?) {
        customLoadView = view
        removeViewAt(LOAD_VIEW_CHILD_POSITION)
        addView(customLoadView ?: defLoadView, LOAD_VIEW_CHILD_POSITION)
    }

    fun setNetErrorView(view: View?) {
        customNetErrorView = view
        removeViewAt(NET_ERROR_VIEW_CHILD_POSITION)
        addView(customNetErrorView ?: defNetErrorView, NET_ERROR_VIEW_CHILD_POSITION)
    }

    fun setNetErrorView(resId: Int) {
        customNetErrorView = LayoutInflater.from(context).inflate(resId, this, false)
        removeViewAt(NET_ERROR_VIEW_CHILD_POSITION)
        addView(customNetErrorView ?: defNetErrorView, NET_ERROR_VIEW_CHILD_POSITION)
    }

    fun showNormalView() {
        showView(NORMAL_VIEW_CHILD_POSITION)
    }

    fun showLoadView() {
        showView(LOAD_VIEW_CHILD_POSITION)
    }

    fun showNetErrorView() {
        showView(NET_ERROR_VIEW_CHILD_POSITION)
    }

    fun showEmptyView() {
        showView(EMPTY_VIEW_CHILD_POSITION)
    }

    private fun showView(childPos: Int) {
        for (i in 0 until childCount) {
            getChildAt(i).visibility = if (i == childPos) View.VISIBLE else View.INVISIBLE
        }
    }
}