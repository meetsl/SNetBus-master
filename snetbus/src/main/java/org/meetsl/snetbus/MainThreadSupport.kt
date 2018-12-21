package org.meetsl.snetbus

import android.os.Looper

interface MainThreadSupport {

    val isMainThread: Boolean

    fun createPoster(netBus: NetBus): Poster

    class AndroidHandlerMainThreadSupport(private val looper: Looper) : MainThreadSupport {

        override val isMainThread: Boolean
            get() = looper == Looper.myLooper()

        override fun createPoster(netBus: NetBus): Poster {
            return HandlerPoster(netBus, looper, 10)
        }
    }

}
