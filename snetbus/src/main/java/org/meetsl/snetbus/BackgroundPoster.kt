package org.meetsl.snetbus

internal class BackgroundPoster(private val netBus: NetBus) : Runnable, Poster {

    private val queue: PendingPostQueue = PendingPostQueue()

    @Volatile
    private var executorRunning: Boolean = false

    override fun enqueue(subscription: Subscription) {
        val pendingPost = PendingPost.obtainPendingPost(subscription)
        synchronized(this) {
            queue.enqueue(pendingPost)
            if (!executorRunning) {
                executorRunning = true
                netBus.executorService.execute(this)
            }
        }
    }

    override fun run() {
        try {
            try {
                while (true) {
                    var pendingPost: PendingPost? = queue.poll(1000)
                    if (pendingPost == null) {
                        synchronized(this) {
                            // Check again, this time in synchronized
                            pendingPost = queue.poll()
                            if (pendingPost == null) {
                                executorRunning = false
                                return
                            }
                        }
                    }
                    netBus.invokeSubscriber(pendingPost!!)
                }
            } catch (e: InterruptedException) {
                //                netBus.getLogger().log(Level.WARNING, Thread.currentThread().getName() + " was interruppted", e);
            }

        } finally {
            executorRunning = false
        }
    }

}
