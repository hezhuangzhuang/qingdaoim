package com.hw.messagemodule.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.messagemodule.R
import com.hw.messagemodule.data.bean.ChatBean
import com.hw.messagemodule.data.bean.MessageBody
import com.hw.messagemodule.data.bean.MessageReal
import com.hw.messagemodule.inject.component.DaggerChatComponent
import com.hw.messagemodule.inject.module.ChatModule
import com.hw.messagemodule.mvp.contract.ChatContract
import com.hw.messagemodule.mvp.presenter.ChatPresenter
import com.hw.provider.router.RouterPath
import com.hw.provider.user.UserContants
import kotlinx.android.synthetic.main.activity_chat.*


/**
 * 聊天界面
 */
@Route(path = RouterPath.Chat.CHAT)
class ChatActivity : BaseMvpActivity<ChatPresenter>(), ChatContract.View {
    //收件人id
    public var receiveId: String? = null

    //收件人名称
    public var receiveName: String? = null

    //是否是群聊
    public var isGroup: Boolean? = false


    override fun initComponent() {
        DaggerChatComponent.builder()
            .activityComponent(mActivityComponent)
            .chatModule(ChatModule())
            .build()
            .inject(this)

        mPresenter.mRootView = this
    }

    override fun initData(bundle: Bundle?) {
        receiveId = intent.getStringExtra(RouterPath.Chat.FILED_RECEIVE_ID)
        receiveName = intent.getStringExtra(RouterPath.Chat.FILED_RECEIVE_NAME)
        isGroup = intent.getBooleanExtra(RouterPath.Chat.FILED_IS_GROUP, false)
    }

    override fun bindLayout() = R.layout.activity_chat

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
    }

    override fun doBusiness() {

    }

    override fun setListeners() {
        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onRightClick(v: View?) {
                val msg = getMessageBody(
                    message = "这是按右键的消息",
                    imgUrl = "",
                    messageType = MessageReal.TYPE_STR
                )
                mPresenter.sendMessage(msg)
            }

            override fun onLeftClick(v: View?) {
                val msg = getMessageBody(
                    message = "这是按左键的消息",
                    imgUrl = "",
                    messageType = MessageReal.TYPE_STR
                )
                mPresenter.sendMessage(msg)
            }

            override fun onTitleClick(v: View?) {

            }
        })
    }

    /**
     * 获取发送的消息
     */
    private fun getMessageBody(
        message: String,//消息内容
        imgUrl: String,//图片路径
        messageType: Int//消息类型
    ): MessageBody {
        val account = SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT, "")

        val disPlayName = SPStaticUtils.getString(UserContants.DISPLAY_NAME, "")

        val messageReal = MessageReal(
            message,
            messageType,
            imgUrl
        )

        val msg = MessageBody(
            account,
            disPlayName,
            receiveId!!,
            receiveName!!,
            if (isGroup!!) {
                MessageBody.TYPE_COMMON
            } else MessageBody.TYPE_PERSONAL,
            messageReal
        )
        return msg
    }


    override fun onError(text: String) {
    }

    /**
     * 第一次加载消息
     */
    override fun firstLoadMessage(chatBean: Array<ChatBean>) {

    }

    /**
     * 发送消息成功
     */
    override fun sendMessageSuccess(chatBean: ChatBean) {
    }

    /**
     * 发送消息失败
     */
    override fun sendMessageFaile(errorMsg: String) {
    }

}
