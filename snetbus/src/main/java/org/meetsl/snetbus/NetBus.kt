package org.meetsl.snetbus

import android.app.Application
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.*
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import org.meetsl.snetbus.lifecycle.LifecycleRegister
import org.meetsl.snetbus.receiver.NetStateChangeReceiver
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ExecutorService

/**
 * Created by meetsl
 *  2018/12/18.
 */
class NetBus constructor(builder: NetBusBuilder) {
    // @Nullable
    private var mainThreadSupport: MainThreadSupport? = null
    // @Nullable
    private var mainThreadPoster: Poster? = null
    private var backgroundPoster: BackgroundPoster
    private var asyncPoster: AsyncPoster
    private var subscriberMethodFinder: SubscriberMethodFinder
    var executorService: ExecutorService
    private var throwSubscriberException: Boolean = false
    private var logSubscriberExceptions: Boolean = false
    private var logNoSubscriberMessages: Boolean = false
    private var indexCount: Int = 0
    private var subscriptions: CopyOnWriteArrayList<Subscription> = CopyOnWriteArrayList()
    private var receiversKeybySubscriber: ConcurrentHashMap<NetStateChangeReceiver, Any>? = null

    private val currentPostingThreadState = object : ThreadLocal<PostingThreadState>() {
        override fun initialValue(): PostingThreadState {
            return PostingThreadState()
        }
    }

    companion object {
        @Volatile
        var defaultInstance: NetBus? = null
        @Volatile
        private var currentNetMode: NetMode? = NetMode.UNAVAILABLE_NET
        private val DEFAULT_BUILDER = NetBusBuilder()
        private var appContext: Application? = null
        private var netCallback: ConnectivityManager.NetworkCallback? = null

        var normalResId = R.layout.layout_normal_default
        var emptyResId = R.layout.layout_empty_default
        var loadResId = R.layout.layout_load_default
        var netErrorResId = R.layout.layout_net_error_default

        /**
         *  初始化 NetBus ,添加对网路的监听
         */
        fun init(appContext: Application) {
            this.appContext = appContext
            currentNetMode = getNetworkType()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val connectivityManager = this.appContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                netCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network?) {
                        super.onAvailable(network)
                        Log.i("Callback_Network", "onAvailable$network")
                    }

                    override fun onLosing(network: Network?, maxMsToLive: Int) {
                        super.onLosing(network, maxMsToLive)
                        Log.i("Callback_Network", "onLosing$network")
                    }

                    override fun onCapabilitiesChanged(network: Network?, networkCapabilities: NetworkCapabilities?) {
                        super.onCapabilitiesChanged(network, networkCapabilities)
                        val ngNet = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        if (ngNet != null && ngNet && currentNetMode != NetMode.CELLULAR) {
                            currentNetMode = NetMode.CELLULAR
                            //网络可用
                            NetBus.getDefault().post()
                        }
                        val wifiNet = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        if (wifiNet != null && wifiNet && currentNetMode != NetMode.WIFI) {
                            currentNetMode = NetMode.WIFI
                            //网络可用
                            NetBus.getDefault().post()
                        }
                        Log.i("Callback_Network", "onCapabilitiesChanged$ngNet----------+$wifiNet")
                        Log.i("Callback_Network", "onCapabilitiesChanged$network")
                    }

                    override fun onLinkPropertiesChanged(network: Network?, linkProperties: LinkProperties?) {
                        super.onLinkPropertiesChanged(network, linkProperties)
                        Log.i("Callback_Network", "onLinkPropertiesChanged$network")
                    }

                    override fun onLost(network: Network?) {
                        super.onLost(network)
                        currentNetMode = NetMode.UNAVAILABLE_NET
                        NetBus.getDefault().post()
                        //网络不可用
                        Log.i("Callback_Network", "onLost$network")
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        Log.i("Callback_Network", "onUnavailable")
                    }
                }
                connectivityManager?.registerNetworkCallback(NetworkRequest.Builder().build(), netCallback)
            }
        }

        fun initNetView(normalResId: Int = R.layout.layout_normal_default,
                        emptyResId: Int = R.layout.layout_empty_default,
                        loadResId: Int = R.layout.layout_load_default,
                        netErrorResId: Int = R.layout.layout_net_error_default) {
            this.normalResId = normalResId
            this.emptyResId = emptyResId
            this.loadResId = loadResId
            this.netErrorResId = netErrorResId
        }

        @Suppress("DEPRECATION")
        private fun getNetworkType(): NetMode {
            val connectivityManager = appContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo
            return when (activeNetworkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> NetMode.WIFI
                ConnectivityManager.TYPE_MOBILE -> NetMode.CELLULAR
                else -> NetMode.UNAVAILABLE_NET
            }
        }

        /**
         * 取消对网络的监听
         */
        fun terminate() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val connectivityManager = appContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                connectivityManager?.unregisterNetworkCallback(netCallback)
            }
            appContext = null
        }

        fun getDefault(): NetBus {
            var instance: NetBus? = defaultInstance
            if (instance == null) {
                synchronized(NetBus::class.java) {
                    instance = NetBus.defaultInstance
                    if (instance == null) {
                        NetBus.defaultInstance = NetBus(DEFAULT_BUILDER)
                        instance = NetBus.defaultInstance
                    }
                }
            }
            return instance!!
        }

    }

    init {
        mainThreadSupport = builder.mainThreadSupport
        mainThreadPoster = mainThreadSupport?.createPoster(this)
        backgroundPoster = BackgroundPoster(this)
        asyncPoster = AsyncPoster(this)
        indexCount = builder.subscriberInfoIndexes?.size ?: 0
        subscriberMethodFinder = SubscriberMethodFinder(builder.subscriberInfoIndexes,
                builder.strictMethodVerification, builder.ignoreGeneratedIndex)
        logSubscriberExceptions = builder.logSubscriberExceptions
        logNoSubscriberMessages = builder.logNoSubscriberMessages
        throwSubscriberException = builder.throwSubscriberException
        executorService = builder.executorService
    }

    fun register(subscriber: Any) {
        val subscriberClass = subscriber.javaClass
        val subscriberMethods = subscriberMethodFinder.findSubscriberMethods(subscriberClass)
        synchronized(this) {
            for (subscriberMethod in subscriberMethods) {
                subscribe(subscriber, subscriberMethod)
            }
        }
    }

    private fun bindLifecycle(subscriber: Any) {
        if (!isRegistered(subscriber)) {
            val lifecycle = LifecycleRegister()
            if (subscriber is FragmentActivity) {
                lifecycle.registerActivity(subscriber)
            }
            if (subscriber is Fragment) {
                lifecycle.registerFragment(subscriber)
            }
        }
    }

    private fun subscribe(subscriber: Any, subscriberMethod: SubscriberMethod) {
        val newSubscription = Subscription(subscriber, subscriberMethod)
        if (subscriptions.contains(newSubscription)) {
            throw NetBusException("Subscriber " + subscriber.javaClass + " already registered to event ")
        }

        //If the subscriber is instance of FragmentActivity or Fragment,we bind to lifecycle here
        if (subscriber is FragmentActivity || subscriber is Fragment) {
            bindLifecycle(subscriber)
        }

        val size = subscriptions.size
        for (i in 0..size) {
            if (i == size || subscriberMethod.priority > subscriptions[i].subscriberMethod.priority) {
                subscriptions.add(i, newSubscription)
                break
            }
        }
    }

    private fun isMainThread(): Boolean {
        return mainThreadSupport?.isMainThread ?: true
    }

    @Synchronized
    fun isRegistered(subscriber: Any): Boolean {
        subscriptions.forEach {
            if (it.subscriber === subscriber)
                return true
        }
        return false
    }

    private fun unsubscribe(subscriber: Any) {
        var size = subscriptions.size
        var i = 0
        while (i < size) {
            val subscription = subscriptions[i]
            if (subscription.subscriber === subscriber) {
                subscription.active = false
                subscriptions.removeAt(i)
                i--
                size--
            }
            i++
        }
    }

    @Synchronized
    fun unregister(subscriber: Any) {

        if (isRegistered(subscriber)) {
            unsubscribe(subscriber)
        } else {
            Log.w("NetBus", "Subscriber to unregister was not registered before: " + subscriber.javaClass)
        }
    }

    fun post() {
        val postingState = currentPostingThreadState.get()!!

        if (!postingState.isPosting) {
            postingState.isMainThread = isMainThread()
            postingState.isPosting = true
            if (postingState.canceled) {
                throw NetBusException("Internal error. Abort state was not reset")
            }
            try {
                postSingleEvent(postingState)
            } finally {
                postingState.isPosting = false
                postingState.isMainThread = false
            }
        }
    }

    fun cancelDelivery() {
        val postingState = currentPostingThreadState.get()!!
        if (!postingState.isPosting) {
            throw NetBusException(
                    "This method may only be called from inside event handling methods on the posting thread")
        } else if (postingState.subscription?.subscriberMethod?.threadMode !== ThreadMode.POSTING) {
            throw NetBusException(" event handlers may only abort the incoming event")
        }
        postingState.canceled = true
    }

    @Throws(Error::class)
    private fun postSingleEvent(postingState: PostingThreadState) {
        val subscriptionFound: Boolean = postSingleEventForEventType(postingState)
        if (!subscriptionFound) {
            if (logNoSubscriberMessages) {
                Log.wtf("NetBus", "No subscribers registered for event")
            }
        }
    }

    private fun postSingleEventForEventType(postingState: PostingThreadState): Boolean {
        if (subscriptions.isNotEmpty()) {
            for (subscription in subscriptions) {
                postingState.subscription = subscription
                var aborted: Boolean
                try {
                    postToSubscription(subscription, postingState.isMainThread)
                    aborted = postingState.canceled
                } finally {
                    postingState.subscription = null
                    postingState.canceled = false
                }
                if (aborted) {
                    break
                }
            }
            return true
        }
        return false
    }


    private fun postToSubscription(subscription: Subscription, isMainThread: Boolean) {
        if (subscription.subscriberMethod.netMode != currentNetMode && currentNetMode != NetMode.UNAVAILABLE_NET) {
            return
        }
        when (subscription.subscriberMethod.threadMode) {
            ThreadMode.POSTING -> invokeSubscriber(subscription)
            ThreadMode.MAIN -> if (isMainThread) {
                invokeSubscriber(subscription)
            } else {
                mainThreadPoster?.enqueue(subscription)
            }
            ThreadMode.MAIN_ORDERED -> if (mainThreadPoster != null) {
                mainThreadPoster?.enqueue(subscription)
            } else {
                // temporary: technically not correct as poster not decoupled from subscriber
                invokeSubscriber(subscription)
            }
            ThreadMode.BACKGROUND -> if (isMainThread) {
                backgroundPoster.enqueue(subscription)
            } else {
                invokeSubscriber(subscription)
            }
            ThreadMode.ASYNC -> asyncPoster.enqueue(subscription)
        }
    }

    fun invokeSubscriber(pendingPost: PendingPost) {
        val subscription = pendingPost.subscription
        PendingPost.releasePendingPost(pendingPost)
        if (subscription != null && subscription.active) {
            invokeSubscriber(subscription)
        }
    }

    fun invokeSubscriber(subscription: Subscription) {
        try {
            subscription.subscriberMethod.method.invoke(subscription.subscriber, currentNetMode != NetMode.UNAVAILABLE_NET)
        } catch (e: InvocationTargetException) {
            handleSubscriberException(subscription, e.cause)
        } catch (e: IllegalAccessException) {
            throw IllegalStateException("Unexpected exception", e)
        }

    }

    private fun handleSubscriberException(subscription: Subscription, cause: Throwable?) {
        if (throwSubscriberException) {
            throw NetBusException("Invoking subscriber failed", cause ?: Throwable())
        }
        if (logSubscriberExceptions) {
            Log.i("", "Could not dispatch event to subscribing class "
                    + subscription.subscriber::class.java, cause)
        }
    }

    /** For ThreadLocal, much faster to set (and get multiple values).  */
    internal class PostingThreadState {
        var isPosting: Boolean = false
        var isMainThread: Boolean = false
        var subscription: Subscription? = null
        var canceled: Boolean = false
    }

    internal fun getExecutorService(): ExecutorService {
        return executorService
    }

    override fun toString(): String {
        return "NetBus[indexCount=$indexCount]"
    }

    fun setNetMode(netMode: NetMode) {
        if (currentNetMode != netMode) {
            currentNetMode = netMode
            post()
        }
    }
}