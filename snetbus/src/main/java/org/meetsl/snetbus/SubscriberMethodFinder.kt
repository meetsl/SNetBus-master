package org.meetsl.snetbus

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by shilong
 *  2018/12/18.
 */
class SubscriberMethodFinder(private val subscriberInfoIndexes: List<SubscriberInfoIndex>?, private val strictMethodVerification: Boolean, private val ignoreGeneratedIndex: Boolean) {
    private val BRIDGE = 0x40
    private val SYNTHETIC = 0x1000
    private val MODIFIERS_IGNORE = Modifier.ABSTRACT or Modifier.STATIC or BRIDGE or SYNTHETIC
    private val POOL_SIZE = 4
    private val FIND_STATE_POOL = arrayOfNulls<FindState>(POOL_SIZE)

    companion object {
        private val METHOD_CACHE = ConcurrentHashMap<Class<*>, List<SubscriberMethod>>()
        fun clearCaches() {
            METHOD_CACHE.clear()
        }
    }

    /**
     *  在注册类中查找注解方法
     */
    fun findSubscriberMethods(subscriberClass: Class<*>): List<SubscriberMethod> {
        var subscriberMethods: List<SubscriberMethod>? = METHOD_CACHE[subscriberClass]
        if (subscriberMethods != null) {
            return subscriberMethods
        }

        subscriberMethods = if (ignoreGeneratedIndex) {
            findUsingReflection(subscriberClass)
        } else {
            findUsingInfo(subscriberClass)
        }
        if (subscriberMethods.isEmpty()) {
            throw NetBusException("Subscriber " + subscriberClass
                    + " and its super classes have no public methods with the @NetSubscribe annotation")
        } else {
            METHOD_CACHE[subscriberClass] = subscriberMethods
            return subscriberMethods
        }
    }

    private fun findUsingReflection(subscriberClass: Class<*>): List<SubscriberMethod> {
        val findState = prepareFindState()
        findState.initForSubscriber(subscriberClass)
        while (findState.clazz != null) {
            findUsingReflectionInSingleClass(findState)
            findState.moveToSuperclass()
        }
        return getMethodsAndRelease(findState)
    }

    private fun findUsingInfo(subscriberClass: Class<*>): List<SubscriberMethod> {
        val findState = prepareFindState()
        findState.initForSubscriber(subscriberClass)
        while (findState.clazz != null) {
            findState.subscriberInfo = getSubscriberInfo(findState)
            if (findState.subscriberInfo != null) {
                val array = findState.subscriberInfo?.subscriberMethods
                if (array != null) {
                    for (subscriberMethod in array) {
                        if (findState.checkAdd(subscriberMethod.method)) {
                            findState.subscriberMethods.add(subscriberMethod)
                        }
                    }
                }
            } else {
                findUsingReflectionInSingleClass(findState)
            }
            findState.moveToSuperclass()
        }
        return getMethodsAndRelease(findState)
    }

    private fun getSubscriberInfo(findState: FindState): SubscriberInfo? {
        if (findState.subscriberInfo != null && findState.subscriberInfo?.superSubscriberInfo != null) {
            val superclassInfo = findState.subscriberInfo?.superSubscriberInfo
            if (findState.clazz == superclassInfo?.subscriberClass) {
                return superclassInfo
            }
        }
        if (subscriberInfoIndexes != null) {
            for (index in subscriberInfoIndexes) {
                val info = index.getSubscriberInfo(findState.clazz!!)
                if (info != null) {
                    return info
                }
            }
        }
        return null
    }

    private fun findUsingReflectionInSingleClass(findState: FindState) {
        var methods: Array<Method>
        try {
            // This is faster than getMethods, especially when subscribers are fat classes like Activities
            methods = findState.clazz!!.declaredMethods
        } catch (th: Throwable) {
            // Workaround for java.lang.NoClassDefFoundError, see https://github.com/greenrobot/EventBus/issues/149
            methods = findState.clazz!!.methods
            findState.skipSuperClasses = true
        }

        for (method in methods) {
            val modifiers = method.modifiers
            if (modifiers and Modifier.PUBLIC != 0 && modifiers and MODIFIERS_IGNORE == 0) {
                val parameterTypes = method.parameterTypes
                if (parameterTypes.isEmpty()) {
                    val subscribeAnnotation = method.getAnnotation(NetSubscribe::class.java)
                    if (subscribeAnnotation != null) {
                        if (findState.checkAdd(method)) {
                            val threadMode = subscribeAnnotation.threadMode
                            val netMode = subscribeAnnotation.netMode
                            findState.subscriberMethods.add(SubscriberMethod(method, threadMode, netMode,
                                    subscribeAnnotation.priority))
                        }
                    }
                } else if (strictMethodVerification && method.isAnnotationPresent(NetSubscribe::class.java)) {
                    val methodName = method.declaringClass.name + "." + method.name
                    throw NetBusException("@NetSubscribe method " + methodName +
                            " must have no parameter but has " + parameterTypes.size)
                }
            } else if (strictMethodVerification && method.isAnnotationPresent(NetSubscribe::class.java)) {
                val methodName = method.declaringClass.name + "." + method.name
                throw NetBusException("$methodName is a illegal @NetSubscribe method: must be public, non-static, and non-abstract")
            }
        }
    }

    private fun prepareFindState(): FindState {
        synchronized(FIND_STATE_POOL) {
            for (i in 0 until POOL_SIZE) {
                val state = FIND_STATE_POOL[i]
                if (state != null) {
                    FIND_STATE_POOL[i] = null
                    return state
                }
            }
        }
        return FindState()
    }

    private fun getMethodsAndRelease(findState: FindState): List<SubscriberMethod> {
        val subscriberMethods = ArrayList(findState.subscriberMethods)
        findState.recycle()
        synchronized(FIND_STATE_POOL) {
            for (i in 0 until POOL_SIZE) {
                if (FIND_STATE_POOL[i] == null) {
                    FIND_STATE_POOL[i] = findState
                    break
                }
            }
        }
        return subscriberMethods
    }

    internal class FindState {
        val subscriberMethods: MutableList<SubscriberMethod> = ArrayList()
        private val methods: MutableList<Any> = mutableListOf()
        private val subscriberClassByMethodKey: MutableMap<String, Class<*>> = HashMap()
        private val methodKeyBuilder = StringBuilder(128)

        private var subscriberClass: Class<*>? = null
        var clazz: Class<*>? = null
        var skipSuperClasses: Boolean = false
        var subscriberInfo: SubscriberInfo? = null

        fun initForSubscriber(subscriberClass: Class<*>) {
            clazz = subscriberClass
            this.subscriberClass = clazz
            skipSuperClasses = false
            subscriberInfo = null
        }

        fun recycle() {
            subscriberMethods.clear()
            methods.clear()
            subscriberClassByMethodKey.clear()
            methodKeyBuilder.setLength(0)
            subscriberClass = null
            clazz = null
            skipSuperClasses = false
            subscriberInfo = null
        }

        fun checkAdd(method: Method): Boolean {
            // 2 level check: 1st level with event type only (fast), 2nd level with complete signature when required.
            // Usually a subscriber doesn't have methods listening to the same event type.
            val existing = methods.contains(method)
            return if (existing) {
                false
            } else {
                methods.add(method)
                checkAddWithMethodSignature(method)
            }
        }

        private fun checkAddWithMethodSignature(method: Method): Boolean {
            methodKeyBuilder.setLength(0)
            methodKeyBuilder.append(method.name)

            val methodKey = methodKeyBuilder.toString()
            val methodClass = method.declaringClass
            val methodClassOld = subscriberClassByMethodKey.put(methodKey, methodClass)
            return if (methodClassOld == null || methodClassOld.isAssignableFrom(methodClass)) {
                // Only add if not already found in a sub class
                true
            } else {
                // Revert the put, old class is further down the class hierarchy
                subscriberClassByMethodKey[methodKey] = methodClassOld
                false
            }
        }

        fun moveToSuperclass() {
            if (skipSuperClasses) {
                clazz = null
            } else {
                clazz = clazz?.superclass
                val clazzName = clazz?.name
                /** Skip system classes, this just degrades performance.  */
                if (clazzName != null && (clazzName.startsWith("java.") || clazzName.startsWith("javax.") || clazzName.startsWith("android."))) {
                    clazz = null
                }
            }
        }
    }
}