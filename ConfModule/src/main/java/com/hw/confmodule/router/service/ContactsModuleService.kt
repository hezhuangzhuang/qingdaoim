package com.hw.confmodule.router.service

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider
import com.hw.baselibrary.common.BaseData
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.provider.IContactsModuleProvider
import io.reactivex.Observable
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/9 14:06
 * 跨模块获取通讯录的数据
 */
class ContactsModuleService : IProvider {
    override fun init(context: Context?) {}

    @Inject
    lateinit var contactsModule: IContactsModuleProvider

    fun getAllPeople(): Observable<BaseData<PeopleBean>> {
        return contactsModule.getAllPeople();
    }
}