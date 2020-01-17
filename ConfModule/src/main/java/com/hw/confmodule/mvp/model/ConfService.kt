package com.hw.confmodule.mvp.model

import com.hw.baselibrary.common.BaseData
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.provider.constacts.impl.ContactsModuleRouteService
import io.reactivex.Observable
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/9 14:27
 */
class ConfService @Inject constructor() {

    /**
     * 获取所有联系人
     */
    fun getAllPeople(): Observable<BaseData<PeopleBean>> {
        return ContactsModuleRouteService.getAllPeople()
    }

}