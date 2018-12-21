package org.meetsl.snetbus

/**
 * Created by shilong
 *  2018/12/19.
 */
/*
@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [POSTING, MAIN_ORDERED, MAIN, BACKGROUND, ASYNC], flag = false)
annotation class ThreadMode

const val POSTING: Int = 0x1001
const val MAIN: Int = 0x1002
const val MAIN_ORDERED: Int = 0x1003
const val BACKGROUND: Int = 0x1004
const val ASYNC: Int = 0x1005*/

enum class ThreadMode {
    POSTING,
    MAIN,
    MAIN_ORDERED,
    BACKGROUND,
    ASYNC
}
