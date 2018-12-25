package org.meetsl.snetbus

/**
 * Created by meetsl
 *  2018/12/19.
 */
enum class NetMode(val transport: Int) {
    // 蜂窝网络
    CELLULAR(0),
    // wifi 网络
    WIFI(1),
    // 蓝牙
    BLUETOOTH(2),
    // 以太网
    ETHERNET(3),
    //vpn
    VPN(4),
    // wifi 唤醒
    WIFI_AWARE(5),
    // 低速无线个域网
    LOWPAN(6),
    //无网络状态
    UNAVAILABLE_NET(-1)
}