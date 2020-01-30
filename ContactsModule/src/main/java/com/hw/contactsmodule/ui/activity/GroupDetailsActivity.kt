package com.hw.contactsmodule.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.contactsmodule.R
import com.hw.contactsmodule.inject.component.DaggerContactsComponent
import com.hw.contactsmodule.inject.module.ContactsModule
import com.hw.contactsmodule.mvp.contract.GroupDetailsContract
import com.hw.contactsmodule.mvp.presenter.GroupDetailsPresenter
import com.hw.contactsmodule.ui.adapter.GroupDetailsAdapter
import com.hw.provider.chat.bean.MessageBody
import com.hw.provider.chat.utils.GreenDaoUtil
import com.hw.provider.conf.ConfContants
import com.hw.provider.eventbus.EventBusUtils
import com.hw.provider.eventbus.EventMsg
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.RouterPath
import com.hw.provider.user.UserContants
import kotlinx.android.synthetic.main.activity_group_details.*
import kotlinx.android.synthetic.main.fragment_contacts.rvList
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.Serializable

@Route(path = RouterPath.Contacts.GROUP_CHAT_DETAILS)
class GroupDetailsActivity : BaseMvpActivity<GroupDetailsPresenter>(), GroupDetailsContract.View {
    //群聊成员适配器
    lateinit var groupDetailsAdapter: GroupDetailsAdapter

    //群组id
    lateinit var groupId: String

    //群组名称
    lateinit var groupName: String

    //是否是群主
    var isCreate: Boolean = false

    override fun initComponent() {
        DaggerContactsComponent.builder().activityComponent(mActivityComponent)
            .contactsModule(ContactsModule())
            .build()
            .inject(this)

        mPresenter.mRootView = this

//        mPresenter.queryPeopleByGroupId(groupId)
        mPresenter.queryGroupCreater(SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT), groupId)
    }

    override fun initData(bundle: Bundle?) {
        EventBus.getDefault().register(this)

        groupId = intent.getStringExtra(RouterPath.Contacts.FILED_RECEIVE_ID)
        groupName = intent.getStringExtra(RouterPath.Contacts.FILED_RECEIVE_NAME)
    }

    override fun bindLayout(): Int = R.layout.activity_group_details

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        groupDetailsAdapter =
            GroupDetailsAdapter(R.layout.item_group_detail, ArrayList<PeopleBean>())

        groupDetailsAdapter.setOnItemChildClickListener { adapter, view, position ->
            ARouter.getInstance()
                .build(RouterPath.Conf.CREATE_CONF)
                //控制的类型
                .withInt(RouterPath.Conf.FILED_CONTROL_TYPE, ConfContants.GROUP_CHAT_ADD_PEOPLE)
                //群组id
                .withString(RouterPath.Conf.FILED_GROUP_ID, groupId)
                //已存在的人员
                .withSerializable(
                    RouterPath.Conf.FILED_EXIST_PEOPLS,
                    groupDetailsAdapter.data as Serializable
                )
                .withInt(RouterPath.Conf.FILED_VIDEO_CONF, -1)
                .navigation()
        }
        rvList.layoutManager = GridLayoutManager(this, 5) as RecyclerView.LayoutManager?
        rvList.adapter = groupDetailsAdapter

        tvGroupName.text = groupName
    }

    override fun doBusiness() {
    }

    override fun setListeners() {
        tvDismissGroup.setOnClickListener {
            if (isCreate) {
                showDeleteGroupChatDialog()
            }
        }

        tvGroupName.setOnClickListener {
            if (isCreate) {
                showModifyGroupNameDialog()
            }
        }

        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onRightClick(v: View?) {
            }

            override fun onTitleClick(v: View?) {
            }

            override fun onLeftClick(v: View?) {
                finish()
            }

        })
    }

    //修改群名称的对话框
    private var modifyGroupNameDialog: MaterialDialog? = null

    /**
     * 显示修改群名称的对话框
     */
    private fun showModifyGroupNameDialog() {
        if (null == modifyGroupNameDialog) {
            modifyGroupNameDialog = MaterialDialog.Builder(this)
                .customView(initModifyGroupNameDialogView(), false)
                .show()
        }

        modifyGroupNameDialog?.show()
    }

    /**
     * 修改群名称的对话框
     */
    private fun initModifyGroupNameDialogView(): View {
        var view = View.inflate(this, R.layout.dialog_modify_group_name, null)
        val etName = view.findViewById<EditText>(R.id.et_content)
        val btConfirm = view.findViewById<TextView>(R.id.bt_confirm)
        val btCancle = view.findViewById<TextView>(R.id.bt_cancle)
        val ivClearText = view.findViewById<ImageView>(R.id.iv_clear_text)

        etName.setText(groupName)

        btConfirm.setOnClickListener {
            if (etName.text.isEmpty()) {
                ToastHelper.showShort("群组名称不能为空")
                return@setOnClickListener
            }
            //修改群名称
            mPresenter.updateGroupName(etName.text.toString(), groupId.toInt())
        }

        btCancle.setOnClickListener {
            modifyGroupNameDialog?.dismiss()
        }

        ivClearText.setOnClickListener {
            etName.setText("")
        }
        return view
    }

    //删除对话框的对话框
    private var deleteGroupChatDialog: MaterialDialog? = null

    /**
     * 删除群聊的对话框
     */
    private fun showDeleteGroupChatDialog() {
        if (null == deleteGroupChatDialog) {
            deleteGroupChatDialog = MaterialDialog.Builder(this)
                .customView(initDeleteGroupChatDialogView(), false)
                .show()
        }

        deleteGroupChatDialog?.show()
    }

    /**
     * 删除群聊的view
     */
    private fun initDeleteGroupChatDialogView(): View {
        var view = View.inflate(this, R.layout.dialog_delete_group_chat, null)
        val btConfirm = view.findViewById<TextView>(R.id.bt_confirm)
        val btCancle = view.findViewById<TextView>(R.id.bt_cancle)

        btConfirm.setOnClickListener {
            //修改群名称
            mPresenter.deleteGroupChat(groupId)
        }

        btCancle.setOnClickListener {
            deleteGroupChatDialog?.dismiss()
        }
        return view
    }

    override fun onError(text: String) {

    }

    override fun queryGroupInfoError(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }

    //显示群聊信息
    override fun showGroupInfo(isCreate: Boolean, groupPeoples: List<PeopleBean>) {
        //不是群主的时候隐藏解散按钮
        tvDismissGroup.isVisible = isCreate
        this.isCreate = isCreate

        groupDetailsAdapter.replaceData(groupPeoples)
    }

    /**
     * 显示群聊详情的界面
     */
    override fun showGroupChatPeople(groupPeoples: List<PeopleBean>) {

    }

    /**
     * 修改群名称结果
     */
    override fun updateGroupNameResult(isSuceess: Boolean, newGroupName: String, groupId: String) {
        //隐藏对话框
        modifyGroupNameDialog?.dismiss()
        EventBusUtils.sendMessage(
            EventMsg.UPDATE_GROUP_CHAT,
            "${groupId},${newGroupName}" as String
        )
        finish()
    }

    /**
     * 删除群聊结果
     */
    override fun deleteGroupChatResult(groupId: String) {
        deleteGroupChatDialog?.dismiss()
        //删除群聊
        GreenDaoUtil.deleteMessageById(groupId)

        EventBusUtils.sendMessage(
            EventMsg.UPDATE_GROUP_CHAT,
            "${groupId},${EventMsg.DELETE_GROUP_CHAT}" as String
        )
        finish()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    /**
     * 主线程中处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun mainEvent(messageEvent: EventMsg<Any>) {
        when (messageEvent.message) {
            //刷新群组人员
            EventMsg.ADD_PEOPLE_TO_GROUPCHAT -> {
                mPresenter.queryGroupCreater(
                    SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT),
                    groupId
                )
            }
        }
    }
}
