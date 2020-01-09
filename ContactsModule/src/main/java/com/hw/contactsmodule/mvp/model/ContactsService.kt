package com.hw.contactsmodule.mvp.model

import com.hw.baselibrary.common.BaseData
import com.hw.baselibrary.net.RetrofitManager
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.rx.scheduler.CustomCompose
import com.hw.contactsmodule.data.api.ContactsApi
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


}