package com.hw.messagemodule.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.gson.Gson
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.messagemodule.data.bean.MessageBody
import com.hw.provider.user.UserContants
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

/**
 * 处理消息
 */
class KotlinMessageSocketService : Service() {

    companion object {
        private val url by lazy {
            Urls.WEBSOCKET_URL + "/im/websocket/im/client?sip=" + SPStaticUtils.getString(
                UserContants.HUAWEI_ACCOUNT
            )
        }
        private val gson by lazy {
            Gson()
        }

        private lateinit var kotlinMessageSocketClient: KotlinMessageSocketClient

        /**
         * 发送消息
         */
        fun sendMessage(message: MessageBody): Boolean {
            try {
                kotlinMessageSocketClient.send(gson.toJson(message))
                return true
            } catch (e: Exception) {
                ToastHelper.showShort("消息发送失败")
                return false
            }
        }

        /**
         * 启动服务
         *
         * @param context
         */
        fun startService(context: Context) {
            val startIntent = Intent(context, KotlinMessageSocketService::class.java)
            context.startService(startIntent)
        }

        /**
         * 停止服务
         *
         * @param context
         */
        fun stopService(context: Context) {
            val stopIntent = Intent(context, KotlinMessageSocketService::class.java)
            context.stopService(stopIntent)
        }

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initSocketClient()
        LogUtils.i("onStartCommand" + kotlinMessageSocketClient)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initSocketClient() {
        kotlinMessageSocketClient =
            object : KotlinMessageSocketClient(URI.create(url)) {
                override fun onMessage(message: String?) {
                    LogUtils.e("MessageSocketService-->onMessage-->$message")
                    handlerMessage(message!!)
                }

                override fun onOpen(handshakedata: ServerHandshake?) {
                    super.onOpen(handshakedata)
                    LogUtils.e("MessageSocketService-->onOpen-->$handshakedata")
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    super.onClose(code, reason, remote)
                    LogUtils.e("MessageSocketService-->onClose-->$reason")
                }

                override fun onError(ex: Exception?) {
                    super.onError(ex)
                    LogUtils.e("MessageSocketService-->onError-->${ex?.message}")
                }
            }
        kotlinMessageSocketClient.connectBlocking()
    }

    /**
     * 处理消息
     */
    private fun handlerMessage(message: String) {
//        ToastHelper.showShort(message)
    }


}
