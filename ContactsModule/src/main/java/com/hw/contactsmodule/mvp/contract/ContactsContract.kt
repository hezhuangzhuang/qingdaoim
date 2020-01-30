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

        fun showOrgan(allOrgan: List<OrganizationBean>)

        //显示组织人员
        fun showOrganPeople(pos: Int, peoples: List<PeopleBean>)

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

        //通过组织id获取组织下的人员
        fun getDepIdConstacts(pos: Int, depId: Int)

        //获取所有群聊
        fun getGroupChat()

        //一键召集会议
        fun createConf(
            confName: String,
            duration: String,
            accessCode: String,
            groupId: String,
            type: Int
        )
    }
}