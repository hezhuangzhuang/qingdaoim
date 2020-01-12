package com.hw.provider.router.provider.impl

import com.hw.baselibrary.common.BaseData
import com.hw.provider.net.respone.contacts.PeopleBean
import io.reactivex.Observable
import com.alibaba.android.arouter.launcher.ARouter
import com.hw.provider.router.provider.IContactsModuleService


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