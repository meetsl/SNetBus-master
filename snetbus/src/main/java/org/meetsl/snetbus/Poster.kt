package org.meetsl.snetbus

interface Poster {

    /**
     * Enqueue an event to be posted for a particular subscription.
     *
     * @param subscription Subscription which will receive the event.
     */
    fun enqueue(subscription: Subscription)
}