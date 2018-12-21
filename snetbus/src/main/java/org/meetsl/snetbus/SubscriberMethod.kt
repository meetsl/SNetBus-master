package org.meetsl.snetbus

import java.lang.reflect.Method

class SubscriberMethod(val method: Method, val threadMode: ThreadMode, val netMode: NetMode, val priority: Int) {
    /** Used for efficient comparison  */
    var methodString: String? = null

    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other is SubscriberMethod -> {
                checkMethodString()
                val otherSubscriberMethod = other as SubscriberMethod?
                otherSubscriberMethod!!.checkMethodString()
                // Don't use method.equals because of http://code.google.com/p/android/issues/detail?id=7811#c6
                methodString == otherSubscriberMethod.methodString
            }
            else -> false
        }
    }

    @Synchronized
    private fun checkMethodString() {
        if (methodString == null) {
            // Method.toString has more overhead, just take relevant parts of the method
            val builder = StringBuilder(64)
            builder.append(method.declaringClass.name)
            builder.append('#').append(method.name)
            methodString = builder.toString()
        }
    }

    override fun hashCode(): Int {
        return method.hashCode()
    }
}