package com.hw.messagemodule.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.net.NetWorkContants
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.NetWorkUtils
import com.hw.baselibrary.utils.PhoneUtils
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.messagemodule.data.bean.WebSocketResult
import com.hw.messagemodule.mvp.model.ChatService
import com.hw.provider.chat.bean.ChatBean
import com.hw.provider.chat.bean.ChatBeanLastMessage
import com.hw.provider.chat.bean.MessageBody
import com.hw.provider.chat.bean.MessageReal
import com.hw.provider.chat.utils.GreenDaoUtil
import com.hw.provider.chat.utils.MessageUtils
import com.hw.provider.eventbus.EventBusUtils
import com.hw.provider.eventbus.EventMsg
import com.hw.provider.router.RouterPath
import com.hw.provider.user.UserContants
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.java_websocket.exceptions.WebsocketNotConnectedException
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.TimeUnit

/**
 * 处理消息
 */
class KotlinMessageSocketService : Service() {
    fun getLine(line: Int): Int {
        return line
    }

    var disposable: Disposable? = null

    fun createDisposable() {
        disposable()

        disposable = Observable.interval(5, TimeUnit.SECONDS)
            .subscribe({
                //轮询socket的状态
                socketPing()
            }, {
                ToastHelper.showShort(ExceptionHandle.handleException(it))
            })
    }

    /**
     * 查询socket的状态
     */
    private fun socketPing() {
        ChatService()
            .ping(
                SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT),
                PhoneUtils.getDeviceId()
            )
            .subscribe({ baseDate ->
                if (NetWorkContants.RESPONSE_CODE == baseDate.responseCode) {
                    //不等于1代表socket不在线
                    if (1 != baseDate.data.isWebSocketOnline) {
                        reConnectSocket()
                    }

                    //不等于0代表在其他地方登录
                    if (0 != baseDate.data.isPhoneRemove) {
                        ToastHelper.showShort("您的账号当前在其他设备登录")
                        //清空用户信息
                        clearUserInfo()

                        //停止服务
                        stopSelf()

                        //跳转到登录界面
                        ARouter.getInstance()
                            .build(RouterPath.UserCenter.PATH_LOGIN)
                            .navigation()
                    }
                }
            }, {
                ToastHelper.showShort(it.toString() + getLine(55))
            })
    }

    /**
     * 清空用户信息
     */
    private fun clearUserInfo() {
        //是否登录
        SPStaticUtils.put(UserContants.HAS_LOGIN, false)

        //显示的名称
        SPStaticUtils.put(UserContants.DISPLAY_NAME, "")

        //用户id
        SPStaticUtils.put(UserContants.USER_ID, -1)

        //登录的用户名密码
        SPStaticUtils.put(UserContants.USER_NAME, "")
        SPStaticUtils.put(UserContants.PASS_WORD, "")

        //华为登录密码
        SPStaticUtils.put(UserContants.HUAWEI_ACCOUNT, "")
        SPStaticUtils.put(UserContants.HUAWEI_PWD, "")

        //华为登录地址
        SPStaticUtils.put(UserContants.HUAWEI_SMC_IP, "")
        SPStaticUtils.put(UserContants.HUAWEI_SMC_PORT, "")
    }

    fun disposable() {
        if (null != disposable) {
            if (!disposable!!.isDisposed) {
                disposable!!.dispose()
                disposable = null
            }
        }
    }

    val userId: String by lazy {
        SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT)
    }

    companion object {
        //0：断开，1：连接，2：正在连接
        var socketStatus: Int = 0

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
                if (e is WebsocketNotConnectedException) {
                    //开始连接
                    //TODO:没有网络时，此处会报错
                    try {
                        if (NetWorkUtils.isConnected() && 2 != socketStatus) {
                            socketStatus = 2
                            val reconnectBlocking = kotlinMessageSocketClient.reconnectBlocking()
                            //如果连接失败则把标识符设成0
                            if (!reconnectBlocking) {
                                //断开
                                socketStatus = 0
                            } else {//如果连接成功则再次发送
                                sendMessage(message)
                            }
                        }
                    } catch (e: Exception) {
                        LogUtils.e("MessageSocketService-->sendMessage-->error-->${e.message}")
                    }
                }
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

    override fun onCreate() {
        super.onCreate()

        createDisposable()

        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable()
        EventBus.getDefault().unregister(this)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initSocketClient()
        LogUtils.i("onStartCommand-->" + kotlinMessageSocketClient)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initSocketClient() {
        kotlinMessageSocketClient =
            object : KotlinMessageSocketClient(URI.create(url)) {
                override fun onMessage(message: String?) {
                    LogUtils.d("MessageSocketService-->onMessage-->$message")
                    handlerMessage(message!!)
                }

                override fun onOpen(handshakedata: ServerHandshake?) {
                    super.onOpen(handshakedata)
                    LogUtils.d("MessageSocketService-->onOpen-->$handshakedata")
                    socketStatus = 1;
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    super.onClose(code, reason, remote)
                    LogUtils.d("MessageSocketService-->onClose-->code-->${code}remote-->${remote}")
                    socketStatus = 0;
                }

                override fun onError(ex: Exception?) {
                    super.onError(ex)
                    LogUtils.d("MessageSocketService-->onError-->${ex?.message}")
                }
            }
        kotlinMessageSocketClient.connectBlocking()
    }

    /**
     * 处理消息
     */
    private fun handlerMessage(message: String) {
        //转换成WebSocketResult
        var webSocketResult = gson.fromJson<WebSocketResult>(message, WebSocketResult::class.java)
        if (webSocketResult.code == -1) {
            return
        }
        when (webSocketResult.event) {
            //离线消息
            WebSocketResult.OFFLINE_MESSAGE ->
                disposeOfflineMessage(webSocketResult)

            //普通消息
            WebSocketResult.MESSAGE ->
                disposeMessage(webSocketResult)
        }
    }

    /**
     * 处理普通消息
     */
    private fun disposeMessage(webSocketResult: WebSocketResult?) {
        var message: MessageBody? = null
        try {
            message = webSocketResult!!.data as MessageBody
        } catch (e: Exception) {
            message = gson.fromJson(gson.toJson(webSocketResult!!.data), MessageBody::class.java)
        }

        //点对点聊天
        saveMessage(message)
    }

    /**
     * 处理离线消息
     */
    private fun disposeOfflineMessage(webSocketResult: WebSocketResult?) {
        var array: ArrayList<Any> = webSocketResult!!.data as ArrayList<Any>;
        array.forEach { item ->
            var message: MessageBody? = null
            message = gson.fromJson(gson.toJson(item), MessageBody::class.java)
            //保存消息
            saveMessage(message)
        }
    }

    /**
     * 保存消息
     */
    private fun saveMessage(message: MessageBody?) {
        if (message!!.type == MessageBody.TYPE_PERSONAL) {
            //保存消息到数据库
            saveChatBeanToDb(message)

            //更新消息
            EventBusUtils.sendMessage(EventMsg.RECEIVE_SINGLE_MESSAGE, message)

            //更新首页消息
            EventBusUtils.sendMessage(EventMsg.REFRESH_HOME_MESSAGE, Any())
        } else {//群聊
            //判断是否是通知，当群组更换名称或解散的时候，type为TYPE_NOTIFY
            val isNotify = MessageReal.TYPE_NOTIFY === message.real.type

            //保存消息到数据库
            saveChatBeanToDb(message)

            //群聊中，消息不是自己发的则做处理
            if (!userId.equals(message.sendId) || isNotify) {
                EventBusUtils.sendMessage(EventMsg.RECEIVE_SINGLE_MESSAGE, message)
            }
        }
    }

    /**
     * 保存个人消息到数据库
     */
    fun saveChatBeanToDb(messageBody: MessageBody) {
        //将messagebody转换成chatbean
        val formMessage: ChatBean = MessageUtils.receiveMessageToChatBean(messageBody)
        //插入到数据库
        val insertChatBean = GreenDaoUtil.insertChatBean(formMessage)

        //将chatbean转成lastchatbean
        val lastMessage: ChatBeanLastMessage = formMessage.toLastMesage()
        //更新最后一条数据
        GreenDaoUtil.insertLastChatBean(lastMessage)
    }

    /**
     * 主线程中处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun mainEvent(messageEvent: EventMsg<Any>) {
        when (messageEvent.message) {
            //网络连接成功
            EventMsg.NET_WORK_CONNECT -> {
//                LogUtils.d(KotlinMessageSocketService::class.java.name + "网络连接成功")
                //socket重新连接
                reConnectSocket()

                //重新启动轮询
                createDisposable()
            }

            //网络断开
            EventMsg.NET_WORK_DISCONNECT -> {
                disposable()
//                LogUtils.d(KotlinMessageSocketService::class.java.name + "网络连接成功")
            }
        }
    }

    /**
     * socket重新连接
     */
    private fun reConnectSocket() {
        socketStatus = 2
        //重新连接
        kotlinMessageSocketClient.reconnect()
    }

}
