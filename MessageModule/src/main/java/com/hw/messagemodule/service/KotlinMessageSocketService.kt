package com.hw.messagemodule.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.common.AppManager
import com.hw.baselibrary.common.BaseApp
import com.hw.baselibrary.net.NetWorkContants
import com.hw.baselibrary.net.RetrofitManager
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.rx.scheduler.CustomCompose
import com.hw.baselibrary.utils.*
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.messagemodule.R
import com.hw.messagemodule.data.api.ChatApi
import com.hw.messagemodule.data.bean.WebSocketResult
import com.hw.messagemodule.mvp.model.ChatService
import com.hw.messagemodule.ui.activity.ChatActivity
import com.hw.provider.chat.bean.ChatBean
import com.hw.provider.chat.bean.ChatBeanLastMessage
import com.hw.provider.chat.bean.MessageBody
import com.hw.provider.chat.bean.MessageReal
import com.hw.provider.chat.utils.GreenDaoUtil
import com.hw.provider.chat.utils.MessageUtils
import com.hw.provider.eventbus.EventBusUtils
import com.hw.provider.eventbus.EventMsg
import com.hw.provider.huawei.commonservice.localbroadcast.CustomBroadcastConstants
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcast
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcastReceiver
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
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

                //轮询华为的状态
                pingHuawei()
            }, {
                ToastHelper.showShort(ExceptionHandle.handleException(it))
            })
    }

    /**
     * 查询华为在线状态
     */
    private fun pingHuawei() {
        RetrofitManager.create(ChatApi::class.java, Urls.FILE_URL)
            .querySiteOnlineState(SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT))
            .compose(CustomCompose())
            .subscribe({ baseData ->
                //请求成功
                if (baseData.code == 0) {
                    LogUtils.d(baseData.toString())
                    if (!baseData.msg.contains("SIP状态在线")) {
                        //登录华为
                        loginHuawei()
                    }
                }
            }, {
                //打印日志
                LogUtils.d("pingHuawei-->responeThrowable" + it.message)
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

    private var isLoginIng = false

    /**
     * 登录华为平台
     */
    private fun loginHuawei() {
        //打印日志
        LogUtils.d("开始重新登陆-->")

        //登陆华为
        HuaweiModuleService.login(
            SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT),
            SPStaticUtils.getString(UserContants.HUAWEI_PWD),
            SPStaticUtils.getString(UserContants.HUAWEI_SMC_IP),
            SPStaticUtils.getString(UserContants.HUAWEI_SMC_PORT)
        )

        //登录中
        isLoginIng = true
    }

    /*华为登录相关start*/
    var mActions = arrayOf<String>(
        CustomBroadcastConstants.LOGIN_SUCCESS,
        CustomBroadcastConstants.LOGIN_FAILED,
        CustomBroadcastConstants.LOGOUT
    )

    private val loginReceiver = object : LocBroadcastReceiver {
        override fun onReceive(broadcastName: String?, obj: Any?) {
            when (broadcastName) {
                CustomBroadcastConstants.LOGIN_SUCCESS -> {
                    LogUtils.i("login success")
                    //登录成功
                    isLoginIng = false
//                    ToastHelper.showShort("华为平台登录成功")
                    //打印日志
                    LogUtils.d("华为平台登录成功")
                }

                CustomBroadcastConstants.LOGIN_FAILED -> {
                    //登录失败
                    isLoginIng = false

                    val errorMessage = obj as String
                    //打印日志
                    LogUtils.d("华为平台登录失败-->$errorMessage")
                    LogUtils.i("login failed,$errorMessage")
                }

                CustomBroadcastConstants.LOGOUT -> LogUtils.i("logout success")

                else -> {
                }
            }
        }
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

        //登录广播
        LocBroadcast.getInstance().registerBroadcast(loginReceiver, mActions)

        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable()

        LocBroadcast.getInstance().unRegisterBroadcast(loginReceiver, mActions)

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

        //保存消息
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
        //点对点聊天
        if (message!!.type == MessageBody.TYPE_PERSONAL) {
            //保存消息到数据库
            saveChatBeanToDb(message)

            //更新消息
            EventBusUtils.sendMessage(EventMsg.RECEIVE_SINGLE_MESSAGE, message)

            //更新首页消息
            EventBusUtils.sendMessage(EventMsg.REFRESH_HOME_MESSAGE, Any())

            sendNotif(message, false)
        } else {//群聊
            //判断是否是通知，当群组更换名称或解散的时候，type为TYPE_NOTIFY
            val isNotify = MessageReal.TYPE_NOTIFY === message.real.type

            //如果是通知
            if (isNotify) {
                //群组重命名
                if (message.real.message.startsWith("群组重命名_")) {
//                    saveGroupName(messageBody.getReceiveId(), message.getMessage().substring(6))

                    saveNewGroupName(message.receiveId, message.real.message.substring(6))
                }
            } else {
                //保存消息到数据库
                saveChatBeanToDb(message)

                //群聊中，消息不是自己发的则做处理
                if (!userId.equals(message.sendId)) {
                    EventBusUtils.sendMessage(EventMsg.RECEIVE_SINGLE_MESSAGE, message)

                    //更新首页消息
                    EventBusUtils.sendMessage(EventMsg.REFRESH_HOME_MESSAGE, Any())

                    sendNotif(message, true)
                }
            }


        }
    }

    /**
     * 保存新群名称
     */
    private fun saveNewGroupName(receiveId: String, newGroupName: String) {
        //通过id获取最后一条数据
        val queryByIdChatBeans = GreenDaoUtil.queryLastChatBeanById(receiveId)
        //修改群名称
        queryByIdChatBeans.conversationUserName = newGroupName

        //保存新的数据
        GreenDaoUtil.insertLastChatBean(queryByIdChatBeans)

        //更新列表
        EventBusUtils.sendMessage(EventMsg.UPDATE_GROUP_CHAT, "${receiveId},${newGroupName}" as String)
    }

    /**
     * 发送notif-start************************************
     */
    private val chatIntent = Intent(BaseApp.context, ChatActivity::class.java)

    /**
     * 发送notif
     *
     * @param eventMsg
     */
    private fun sendNotif(message: MessageBody?, isGroupChat: Boolean) {
        //获取消息类型
        val type = message!!.real.type

        if (MessageReal.TYPE_VIDEO_CALL === type || MessageReal.TYPE_VOICE_CALL === type) {
            return
        }

        //获取当前的activity
        val activity = AppManager.instance.getCurActivity()

        if (activity is ChatActivity) {
            //            ToastUtil.showShortToast(getApplicationContext(), "当前是聊天界面");
            return
        } else {
            //            ToastUtil.showShortToast(getApplicationContext(), "当前界面");
        }

        //获取notifid
        val notifId = if (isGroupChat) message.receiveId.toInt() else message.sendId.toInt()

        //获得消息
        val notifText = getNotifText(message, isGroupChat)
        NotificationUtils.notify(
            notifId,
            object : NotificationUtils.Func1<Void, NotificationCompat.Builder> {
                override fun call(param: NotificationCompat.Builder): Void? {
                    //群聊
                    if (isGroupChat) {
                        chatIntent.putExtra(
                            RouterPath.Chat.FILED_RECEIVE_ID,
                            message.receiveId
                        )
                        chatIntent.putExtra(
                            RouterPath.Chat.FILED_RECEIVE_NAME,
                            message.receiveName
                        )
                        chatIntent.putExtra(RouterPath.Chat.FILED_IS_GROUP, true)
                    } else {
                        chatIntent.putExtra(
                            RouterPath.Chat.FILED_RECEIVE_ID,
                            message.sendId
                        )
                        chatIntent.putExtra(
                            RouterPath.Chat.FILED_RECEIVE_NAME,
                            message.sendName
                        )
                        chatIntent.putExtra(RouterPath.Chat.FILED_IS_GROUP, false)
                    }
                    param.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(if (isGroupChat) message.receiveName else message.sendName)
                        .setContentText(notifText)
                        //使用默认的声音和震动
                        .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                        //设置该通知的优先级
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        //状态栏的通知
                        .setContentIntent(
                            PendingIntent.getActivity(
                                BaseApp.context,
                                0,
                                chatIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        )
                        //浮动通知（弹窗式通知）
                        //                        .setFullScreenIntent(PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT), false)
                        .setAutoCancel(true)
                    return null
                }
            })
    }

    private fun getNotifText(message: MessageBody?, isGroupChat: Boolean): String {
        val content = ""
        /*1:文字; 2:图片; 3:表情； 4：语音*/
        when (message!!.real.type) {
            1 -> {
                if (isGroupChat) return message.sendName + ": " + message!!.real.message else return message!!.real.message
            }

            2 -> {
                if (isGroupChat) return message.sendName + ": [图片]" else return "[图片]"
            }

            3 -> {
                if (isGroupChat) return message.sendName + ": [表情]" else return "[表情]"
            }

            4 -> {
                if (isGroupChat) return message.sendName + ": [文件]" else return "[文件]"
            }

            else -> return content
        }
    }

    /**
     * 发送notif-end************************************
     */

    /**
     * 保存个人消息到数据库
     */
    fun saveChatBeanToDb(messageBody: MessageBody) {
        //将messagebody转换成chatbean
        val formMessage: ChatBean =
            //群聊
            if (messageBody.type == MessageBody.TYPE_COMMON)
                MessageUtils.receiveGroupMessageToChatBean(messageBody)
            else
                MessageUtils.receiveMessageToChatBean(messageBody)

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
