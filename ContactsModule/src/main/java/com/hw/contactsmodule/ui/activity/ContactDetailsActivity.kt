package com.hw.contactsmodule.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.ui.activity.BaseActivity
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.contactsmodule.R
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
import com.hw.provider.user.UserContants
import kotlinx.android.synthetic.main.activity_contact_details.*

/**
 * 联系人详情
 */
@Route(path = RouterPath.Contacts.CONTACT_DETAILS)
class ContactDetailsActivity : BaseActivity(), View.OnClickListener {

    //联系人名称
    lateinit var name: String

    //联系人id
    lateinit var id: String

    override fun initData(bundle: Bundle?) {
        id = intent.getStringExtra(RouterPath.Contacts.FILED_RECEIVE_ID)
        name = intent.getStringExtra(RouterPath.Contacts.FILED_RECEIVE_NAME)
    }

    override fun bindLayout(): Int = R.layout.activity_contact_details

    override fun initView(savedInstanceState: Bundle?, contentView: View) {

        tvName.text = name
    }

    override fun doBusiness() {
    }

    override fun setListeners() {
        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {
                finish()
            }

            override fun onRightClick(v: View?) {
            }

            override fun onTitleClick(v: View?) {
            }
        })
        flSendMsg.setOnClickListener(this)
        flVideoCall.setOnClickListener(this)
        flVoiceCall.setOnClickListener(this)
    }

    override fun onError(text: String) {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            //发送消息
            R.id.flSendMsg ->
                ARouter.getInstance()
                    .build(RouterPath.Chat.CHAT)
                    .withString(RouterPath.Chat.FILED_RECEIVE_ID, id)
                    .withString(RouterPath.Chat.FILED_RECEIVE_NAME, name)
                    .withBoolean(RouterPath.Chat.FILED_IS_GROUP, false)
                    .navigation()

            //视频呼叫
            R.id.flVideoCall ->{
                HuaweiModuleService.callSite(id, true)
            }


            //语音呼叫
            R.id.flVoiceCall ->
                HuaweiModuleService.callSite(id, false)

        }
    }
}
