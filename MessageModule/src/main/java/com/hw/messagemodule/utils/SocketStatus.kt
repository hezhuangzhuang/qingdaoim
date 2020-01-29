package com.hw.messagemodule.utils

/**
 * socket的连接状态
 * 0，已断开，
 * 1，已连接
 * 2，正在连接中
 */
enum class SocketStatus(var code: Int) {

    CLOSE(0), CONNECT(1), CONNECTING(2)
}