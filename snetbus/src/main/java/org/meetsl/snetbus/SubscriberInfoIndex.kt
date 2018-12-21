package org.meetsl.snetbus

/**
 * Created by shilong
 *  2018/12/19.
 */
interface SubscriberInfoIndex {
    fun getSubscriberInfo(subscriberClass: Class<*>): SubscriberInfo?
}