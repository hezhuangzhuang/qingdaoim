package com.hw.provider.router.provider.constacts.impl

import com.alibaba.android.arouter.launcher.ARouter
import com.hw.baselibrary.common.BaseData
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.provider.constacts.IContactsModuleService
import io.reactivex.Observable


/**
 * 通讯录供第三方调用的方法
 */
object ContactsModuleRouteService {

    fun getAllPeople()
            : Observable<BaseData<PeopleBean>> {

        val navigation = ARouter.getInstance().navigation(IContactsModuleService::class.java)

        return navigation.getAllPeople()
    }
}