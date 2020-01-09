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
        fun showAllPeople(allPeople: List<PeopleBean>)

        fun showOrgan(allOrgan: List<OrganizationBean>)

        fun showOrganPeople(pos: Int,peoples: List<PeopleBean>)

        fun showGroupChat(groupChats: List<GroupChatBean>)

        fun showError(errorMsg: String)

        fun showEmptyView()
    }

    interface Presenter {
        fun getAllPeople()

        fun getAllOrganizations()

        fun getDepIdConstacts(pos: Int, depId: Int)

        fun getGroupChat()
    }
}