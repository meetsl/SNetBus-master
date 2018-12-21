package org.meetsl.snetbus

internal class AsyncPoster(private val netBus: NetBus) : Runnable, Poster {

    private val queue: PendingPostQueue = PendingPostQueue()

    override fun enqueue(subscription: Subscription) {
        val pendingPost = PendingPost.obtainPendingPost(subscription)
        queue.enqueue(pendingPost)
        netBus.executorService.execute(this)
    }

    override fun run() {
        val pendingPost = queue.poll() ?: throw IllegalStateException("No pending post available")
        netBus.invokeSubscriber(pendingPost)
    }
}