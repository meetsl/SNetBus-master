package org.meetsl.snetbus

/**
 * Created by meetsl
 *  2018/12/19.
 */
interface SubscriberInfoIndex {
    fun getSubscriberInfo(subscriberClass: Class<*>): SubscriberInfo?
}