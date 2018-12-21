package org.meetsl.snetbus

class NetBusException : RuntimeException {
    private val serialVersionUID = -2912559384646531479L

    constructor(detailMessage: String) : super(detailMessage) {}

    constructor(throwable: Throwable) : super(throwable) {}

    constructor(detailMessage: String, throwable: Throwable) : super(detailMessage, throwable) {}
}