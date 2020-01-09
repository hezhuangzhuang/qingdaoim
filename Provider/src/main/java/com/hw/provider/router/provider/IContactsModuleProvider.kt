package com.hw.provider.router.provider

import com.alibaba.android.arouter.facade.template.IProvider
import com.hw.baselibrary.common.BaseData
import com.hw.provider.net.respone.contacts.PeopleBean
import io.reactivex.Observable
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/9 13:53
 * 获取通讯录的服务接口
 */
public interface IContactsModuleProvider : IProvider {

    /**
     * 获取所有联系人
     */
    fun getAllPeople(): Observable<BaseData<PeopleBean>>

}