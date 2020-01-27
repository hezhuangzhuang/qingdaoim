package com.hw.messagemodule.utils

/**
 * socket的连接状态
 */
enum class SocketStatus(var code: Int) {
    CLOSE(0), CONNECT(1), CONNECTING(2)
}