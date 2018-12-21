package org.meetsl.snetbus

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock

open class HandlerPoster constructor(private val netBus: NetBus, looper: Looper, private val maxMillisInsideHandleMessage: Int) : Handler(looper), Poster {

    private val queue: PendingPostQueue = PendingPostQueue()
    private var handlerActive: Boolean = false

    override fun enqueue(subscription: Subscription) {
        val pendingPost = PendingPost.obtainPendingPost(subscription)
        synchronized(this) {
            queue.enqueue(pendingPost)
            if (!handlerActive) {
                handlerActive = true
                if (!sendMessage(obtainMessage())) {
                    throw NetBusException("Could not send handler message")
                }
            }
        }
    }

    override fun handleMessage(msg: Message) {
        var rescheduled = false
        try {
            val started = SystemClock.uptimeMillis()
            while (true) {
                var pendingPost = queue.poll()
                if (pendingPost == null) {
                    synchronized(this) {
                        // Check again, this time in synchronized
                        pendingPost = queue.poll()
                        if (pendingPost == null) {
                            handlerActive = false
                            return
                        }
                    }
                }
                netBus.invokeSubscriber(pendingPost!!)
                val timeInMethod = SystemClock.uptimeMillis() - started
                if (timeInMethod >= maxMillisInsideHandleMessage) {
                    if (!sendMessage(obtainMessage())) {
                        throw NetBusException("Could not send handler message")
                    }
                    rescheduled = true
                    return
                }
            }
        } finally {
            handlerActive = rescheduled
        }
    }
}