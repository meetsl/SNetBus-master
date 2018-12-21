package org.meetsl.snetbus

class Subscription(val subscriber: Any, val subscriberMethod: SubscriberMethod) {
    /**
     * Becomes false as soon as [NetBus.unregister] is called, which is checked by queued event delivery
     * [NetBus.invokeSubscriber] to prevent race conditions.
     */
    @Volatile
    var active: Boolean = false

    init {
        active = true
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Subscription) {
            val otherSubscription = other as Subscription?
            subscriber === otherSubscription?.subscriber && subscriberMethod == otherSubscription.subscriberMethod
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return subscriber.hashCode() + (subscriberMethod.methodString?.hashCode() ?: 0)
    }
}