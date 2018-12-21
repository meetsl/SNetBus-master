package org.meetsl.snetbus

interface SubscriberInfo {
    val subscriberClass: Class<*>

    val subscriberMethods: Array<SubscriberMethod>

    val superSubscriberInfo: SubscriberInfo

    fun shouldCheckSuperclass(): Boolean
}