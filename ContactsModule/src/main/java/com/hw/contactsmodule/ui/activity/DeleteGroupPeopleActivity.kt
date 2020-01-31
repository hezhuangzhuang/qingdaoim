package com.hw.contactsmodule.ui.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.contactsmodule.R
import com.hw.contactsmodule.inject.component.DaggerContactsComponent
import com.hw.contactsmodule.inject.module.ContactsModule
import com.hw.contactsmodule.mvp.contract.DeleteGroupPeopleContract
import com.hw.contactsmodule.mvp.presenter.DeleteGroupPeoplePresenter
import com.hw.contactsmodule.ui.adapter.DeleteGroupPeopleAdapter
import com.hw.provider.eventbus.EventBusUtils
import com.hw.provider.eventbus.EventMsg
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.RouterPath
import com.hw.provider.user.UserContants
import kotlinx.android.synthetic.main.activity_group_details.*
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.fragment_contacts.rvList

/**
 * 删除群组成员
 */
@Route(path = RouterPath.Contacts.DELETE_GROUP_PEOPLE)
class DeleteGroupPeopleActivity : BaseMvpActivity<DeleteGroupPeoplePresenter>(),
    DeleteGroupPeopleContract.View {

    override fun initComponent() {
        DaggerContactsComponent.builder()
            .activityComponent(mActivityComponent)
            .contactsModule(ContactsModule())
            .build()
            .inject(this)

        mPresenter.mRootView = this
    }

    lateinit var deleteGroupPeopleAdapter: DeleteGroupPeopleAdapter

    var allPeoples = ArrayList<PeopleBean>()

    val selectPeoples by lazy {
        ArrayList<PeopleBean>()
    }

    lateinit var groupId: String

    override fun initData(bundle: Bundle?) {
        var tempPeoples =
            intent.getSerializableExtra(RouterPath.Contacts.FILED_ALL_PEOPLE) as ArrayList<PeopleBean>

        allPeoples = tempPeoples.filter {
            !it.name.isEmpty() && !it.name.equals(SPStaticUtils.getString(UserContants.DISPLAY_NAME))
        } as ArrayList<PeopleBean>

        groupId = intent.getStringExtra(RouterPath.Contacts.FILED_GROUP_ID)
    }

    override fun bindLayout(): Int = R.layout.activity_delete_group_people

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        deleteGroupPeopleAdapter =
            DeleteGroupPeopleAdapter(R.layout.item_delete_group_people, allPeoples)

        deleteGroupPeopleAdapter.setOnItemClickListener { adapter, view, position ->
            itemClick(position)
        }

        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = deleteGroupPeopleAdapter
    }

    /**
     * item的点击事件
     */
    private fun itemClick(position: Int) {
        var peopleBean = deleteGroupPeopleAdapter.getItem(position)
        //选中状态取反
        peopleBean?.isCheck = !peopleBean!!.isCheck

        if (peopleBean.isCheck) {
            selectPeoples.add(peopleBean)
        } else {
            selectPeoples.remove(peopleBean)
        }
        deleteGroupPeopleAdapter.notifyItemChanged(position)
    }


    override fun doBusiness() {

    }

    override fun setListeners() {
        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {
                finish()
            }

            override fun onRightClick(v: View?) {
                var ids = selectPeoples.joinToString(separator = ",") {
                    it.id
                }
                mPresenter.deletePeoples(groupId, ids)
            }

            override fun onTitleClick(v: View?) {
            }
        })
    }

    override fun onError(text: String) {

    }

    override fun deletePeopleSuccess() {
        ToastHelper.showShort("删除群组成员成功")
        EventBusUtils.sendMessage(EventMsg.ADD_PEOPLE_TO_GROUPCHAT, "")
        finish()
    }

    override fun deletePeopleFailed(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }
}
