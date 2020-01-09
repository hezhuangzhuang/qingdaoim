package com.hw.messagemodule.ui.activity

import android.os.Bundle
import android.view.View
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.messagemodule.R
import com.hw.messagemodule.mvp.presenter.ChatPresenter

/**
 * 聊天界面
 */
class ChatActivity : BaseMvpActivity<ChatPresenter>() {
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

}
