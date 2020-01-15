package com.hw.contactsmodule.arouter

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.hw.baselibrary.common.BaseData
import com.hw.contactsmodule.mvp.model.ContactsService
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.constacts.IContactsModuleService
import io.reactivex.Observable
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/9 13:58
 */
@Route(path = RouterPath.Contacts.CONTACTS_MODULE_SERVICE)
class ContactsModuleServiceImp @Inject constructor() :
    IContactsModuleService {

    /**
     * 获取所有的联系人
     */
    override fun getAllPeople(): Observable<BaseData<PeopleBean>> {
        return ContactsService().queryAllPeople()
    }

    override fun init(context: Context?) {

    }
}