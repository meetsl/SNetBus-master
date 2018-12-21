package org.meetsl.snetbus

import java.util.ArrayList

class PendingPost private constructor(var subscription: Subscription?) {
    var next: PendingPost? = null

    companion object {
        private val pendingPostPool = ArrayList<PendingPost>()

        fun obtainPendingPost(subscription: Subscription): PendingPost {

            synchronized(pendingPostPool) {
                val size = pendingPostPool.size
                if (size > 0) {
                    val pendingPost = pendingPostPool.removeAt(size - 1)
                    pendingPost.subscription = subscription
                    pendingPost.next = null
                    return pendingPost
                }
            }
            return PendingPost(subscription)
        }

        fun releasePendingPost(pendingPost: PendingPost) {
            pendingPost.subscription = null
            pendingPost.next = null
            synchronized(pendingPostPool) {
                // Don't let the pool grow indefinitely
                if (pendingPostPool.size < 10000) {
                    pendingPostPool.add(pendingPost)
                }
            }
        }
    }
}