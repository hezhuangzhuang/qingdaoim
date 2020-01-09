package com.hw.contactsmodule.data.api

import com.hw.baselibrary.common.BaseData
import com.hw.provider.net.respone.contacts.GroupChatBean
import com.hw.provider.net.respone.contacts.OrganizationBean
import com.hw.provider.net.respone.contacts.PeopleBean
import io.reactivex.Observable
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *author：pc-20171125
 *data:2020/1/8 16:05
 */
interface ContactsApi {
    /**
     * 获取用户列表
     */
    //需要添加头
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("im/mobile/account/findAllByFirstChar")
    fun getAllConstacts(): Observable<BaseData<PeopleBean>>

    /**
     * 获取所有组织
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/department/findAll")
    fun getAllOrganizations(): Observable<BaseData<OrganizationBean>>

    /**
     * 根据组织id获取所属人员
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/account/getByDepId")
    fun getDepIdConstacts(@Query("depId") depId: Int): Observable<BaseData<PeopleBean>>


    /**
     * 查询所有群组
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/group/getGroupsByAccount")
     fun getGroupsById(@Query("id") id: Int): Observable<BaseData<GroupChatBean>>
}