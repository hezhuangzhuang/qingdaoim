package com.hw.contactsmodule.mvp.contract

import com.hw.baselibrary.common.IBaseView
import com.hw.provider.net.respone.contacts.GroupChatBean
import com.hw.provider.net.respone.contacts.OrganizationBean
import com.hw.provider.net.respone.contacts.PeopleBean

/**
 *author：pc-20171125
 *data:2020/1/8 11:55
 */
interface ContactsContract {

    interface View : IBaseView {
        fun showAllPeople(allPeople: List<PeopleBean>,onlineNumber:Int)

        //显示所有组织
        fun showAllOrgan(allOrgan: List<OrganizationBean>)

        //显示子组织人员
        fun showChildOrganPeople(pos: Int, depId: Int, peoples: List<OrganizationBean>)

        //查询子组织失败
        fun queryChildOrganPeopleError(errorMsg: String)

        //显示组织人员
        fun showOrganPeople(pos: Int, peoples: List<PeopleBean>)

        //查询子组织失败
        fun queryOrganPeopleError(errorMsg: String)

        fun showGroupChat(groupChats: List<GroupChatBean>)

        fun showError(errorMsg: String)

        fun showEmptyView()
    }

    interface Presenter {
        //获取所有联系人
        fun getAllPeople()

        //获取所有组织
        fun getAllOrganizations()


        //获取子组织
        fun getChildOrganizations(pos: Int, depId: Int)

        //通过组织id获取组织下的人员
        fun getDepIdConstacts(pos: Int, depId: Int)

        //获取所有群聊
        fun getGroupChat()

        //群组一键召集会议
        fun groupChatOneCreateConf(
            confName: String,
            duration: String,
            accessCode: String,
            groupId: String,
            type: Int
        )

        //组织机构一键召集会议
        fun organizationOneCreateConf(
            confName: String,//会议名称
            duration: String,//会议时长
            accessCode: String,//会议接入码
            groupId: String,//群组id
            type: Int//会议类型
        )
    }
}