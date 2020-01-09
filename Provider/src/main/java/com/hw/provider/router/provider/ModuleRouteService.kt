package com.hw.provider.router.provider

import com.hw.baselibrary.common.BaseData
import com.hw.provider.net.respone.contacts.PeopleBean
import io.reactivex.Observable
import com.alibaba.android.arouter.launcher.ARouter


/**
 * 获取
 */
object ModuleRouteService {
    fun getAllPeople()
            : Observable<BaseData<PeopleBean>> {
        val navigation = ARouter.getInstance().navigation(IContactsModuleService::class.java)

        return navigation.getAllPeople()
    }
}