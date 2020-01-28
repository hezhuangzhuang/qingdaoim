package com.hw.contactsmodule.mvp.model

import com.hw.baselibrary.common.BaseData
import com.hw.baselibrary.net.RetrofitManager
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.rx.scheduler.CustomCompose
import com.hw.contactsmodule.data.api.ContactsApi
import com.hw.contactsmodule.data.bean.GroupDetailsBean
import com.hw.provider.net.respone.contacts.GroupChatBean
import com.hw.provider.net.respone.contacts.OrganizationBean
import com.hw.provider.net.respone.contacts.PeopleBean
import io.reactivex.Observable
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/8 11:57
 * 获取通讯录的接口
 */
class ContactsService @Inject constructor() {

    /**
     * 获取全部联系人
     */
    fun queryAllPeople(): Observable<BaseData<PeopleBean>> {
        return RetrofitManager
            .create(ContactsApi::class.java, Urls.WEBSOCKET_URL)
            .getAllConstacts()
            .compose(CustomCompose())
    }

    /**
     * 获取特别联系人
     */
    fun queryGovAccounts(): Observable<BaseData<PeopleBean>> {
        return RetrofitManager
            .create(ContactsApi::class.java, Urls.WEBSOCKET_URL)
            .getGovAccounts()
            .compose(CustomCompose())
    }

    /**
     * 获取组织结构
     */
    fun queryAllOrganizations(): Observable<BaseData<OrganizationBean>> {
        return RetrofitManager
            .create(ContactsApi::class.java, Urls.WEBSOCKET_URL)
            .getAllOrganizations()
            .compose(CustomCompose())
    }

    /**
     * 通过组织id查询下属成员
     */
    fun queryByDepIdConstacts(depId: Int): Observable<BaseData<PeopleBean>> {
        return RetrofitManager
            .create(ContactsApi::class.java, Urls.WEBSOCKET_URL)
            .getDepIdConstacts(depId)
            .compose(CustomCompose())
    }

    /**
     * 通过id查询所有群组
     */
    fun queryGroupsById(id: Int): Observable<BaseData<GroupChatBean>> {
        return RetrofitManager
            .create(ContactsApi::class.java, Urls.WEBSOCKET_URL)
            .getGroupsById(id)
            .compose(CustomCompose())
    }

    /**
     * 通过群组id查询群组人员
     */
    fun getGroupIdConstacts(groupId: String): Observable<BaseData<PeopleBean>> {
        return RetrofitManager
            .create(ContactsApi::class.java, Urls.WEBSOCKET_URL)
            .getGroupIdConstacts(groupId)
            .compose(CustomCompose())
    }

    /**
     * 通过群组id查询群主
     */
    fun queryGroupCreater(siteUri: String, groupId: String): Observable<GroupDetailsBean> {
        return RetrofitManager
            .create(ContactsApi::class.java, Urls.WEBSOCKET_URL)
            .queryGroupCreater(siteUri, groupId)
            .compose(CustomCompose())
    }

    /**
     * 修改群名称
     */
    fun updateGroupName(
        groupId: Int,
        newName: String
    ): Observable<GroupDetailsBean> {
        return RetrofitManager
            .create(ContactsApi::class.java, Urls.WEBSOCKET_URL)
            .updateGroupName(groupId, newName)
            .compose(CustomCompose())
    }

    /**
     * 删除群聊
     */
    fun deleteGroupChat(
        groupId: String
    ): Observable<GroupDetailsBean> {
        return RetrofitManager
            .create(ContactsApi::class.java, Urls.WEBSOCKET_URL)
            .deleteGroupChat(groupId)
            .compose(CustomCompose())
    }


}