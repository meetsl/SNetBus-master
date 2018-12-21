package org.meetsl.snetbus

import android.os.Looper
import java.util.ArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by shilong
 *  2018/12/19.
 */
class NetBusBuilder {
    private val DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool()

    var logSubscriberExceptions = true
    var logNoSubscriberMessages = true
    var throwSubscriberException: Boolean = false
    var ignoreGeneratedIndex: Boolean = false
    var strictMethodVerification: Boolean = false
    var executorService = DEFAULT_EXECUTOR_SERVICE
    var skipMethodVerificationForClasses: MutableList<Class<*>>? = null
    var subscriberInfoIndexes: MutableList<SubscriberInfoIndex>? = null
    var mainThreadSupport: MainThreadSupport? = null

    /** Default: true  */
    fun logSubscriberExceptions(logSubscriberExceptions: Boolean): NetBusBuilder {
        this.logSubscriberExceptions = logSubscriberExceptions
        return this
    }

    /** Default: true  */
    fun logNoSubscriberMessages(logNoSubscriberMessages: Boolean): NetBusBuilder {
        this.logNoSubscriberMessages = logNoSubscriberMessages
        return this
    }

    fun throwSubscriberException(throwSubscriberException: Boolean): NetBusBuilder {
        this.throwSubscriberException = throwSubscriberException
        return this
    }

    fun executorService(executorService: ExecutorService): NetBusBuilder {
        this.executorService = executorService
        return this
    }

    fun skipMethodVerificationFor(clazz: Class<*>): NetBusBuilder {
        if (skipMethodVerificationForClasses == null) {
            skipMethodVerificationForClasses = ArrayList()
        }
        skipMethodVerificationForClasses?.add(clazz)
        return this
    }

    fun ignoreGeneratedIndex(ignoreGeneratedIndex: Boolean): NetBusBuilder {
        this.ignoreGeneratedIndex = ignoreGeneratedIndex
        return this
    }

    fun strictMethodVerification(strictMethodVerification: Boolean): NetBusBuilder {
        this.strictMethodVerification = strictMethodVerification
        return this
    }

    fun addIndex(index: SubscriberInfoIndex): NetBusBuilder {
        if (subscriberInfoIndexes == null) {
            subscriberInfoIndexes = ArrayList()
        }
        subscriberInfoIndexes?.add(index)
        return this
    }

    internal fun getMainThreadSupport(): MainThreadSupport? {
        return if (mainThreadSupport != null) {
            mainThreadSupport
        } else {
            val looperOrNull = getAndroidMainLooperOrNull()
            if (looperOrNull == null)
                null
            else
                MainThreadSupport.AndroidHandlerMainThreadSupport(looperOrNull as Looper)
        }
    }

    private fun getAndroidMainLooperOrNull(): Any? {
        return try {
            Looper.getMainLooper()
        } catch (e: RuntimeException) {
            // Not really a functional Android (e.g. "Stub!" maven dependencies)
            null
        }
    }

    fun installDefaultNetBus(): NetBus {
        synchronized(NetBus::class.java) {
            if (NetBus.defaultInstance != null) {
                throw NetBusException("Default instance already exists." + " It may be only set once before it's used the first time to ensure consistent behavior.")
            }
            NetBus.defaultInstance = build()
            return NetBus.defaultInstance!!
        }
    }

    /** Builds an NetBus based on the current configuration.  */
    fun build(): NetBus {
        return NetBus(this)
    }
}