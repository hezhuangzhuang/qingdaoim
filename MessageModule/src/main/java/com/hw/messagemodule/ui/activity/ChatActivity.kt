package com.hw.messagemodule.ui.activity

import android.os.Bundle
import android.view.View
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.messagemodule.R
import com.hw.messagemodule.data.bean.ChatBean
import com.hw.messagemodule.mvp.contract.ChatContract
import com.hw.messagemodule.mvp.presenter.ChatPresenter

/**
 * 聊天界面
 */
class ChatActivity : BaseMvpActivity<ChatPresenter>(), ChatContract.View {
    override fun initComponent() {
    }

    override fun initData(bundle: Bundle?) {
    }

    override fun bindLayout() = R.layout.activity_chat

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
    }

    override fun doBusiness() {

    }

    override fun setListeners() {
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
