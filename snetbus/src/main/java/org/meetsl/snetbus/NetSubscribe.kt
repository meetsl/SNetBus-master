package org.meetsl.snetbus

/**
 * Created by shilong
 *  2018/12/19.
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class NetSubscribe(
        val netMode: NetMode = NetMode.LOWPAN,
        val threadMode: ThreadMode = ThreadMode.POSTING,
        val priority: Int = 0
)