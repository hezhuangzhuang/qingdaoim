package com.hw.contactsmodule.data.api

import com.hw.baselibrary.common.BaseData
import com.hw.contactsmodule.data.bean.GroupDetailsBean
import com.hw.provider.net.respone.contacts.GroupChatBean
import com.hw.provider.net.respone.contacts.OrganizationBean
import com.hw.provider.net.respone.contacts.PeopleBean
import io.reactivex.Observable
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

/**
 *author：pc-20171125
 *data:2020/1/8 16:05
 */
interface ContactsApi {
    /**
     * 获取特别用户列表
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/account/findAllGovAccount")
    fun getGovAccounts(): Observable<BaseData<PeopleBean>>

    /**
     * 获取用户列表
     */
    //需要添加头
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("im/mobile/account/findAllByFirstChar")
    fun getAllConstacts(): Observable<BaseData<PeopleBean>>

    /**
     * 获取smc上的列表，包含在线状态
     */
    //需要添加头
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("conf/queryOnlineSiteList")
    fun queryOnlineSiteList(): Observable<BaseData<PeopleBean>>

    /**
     * 获取所有组织
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/department/findAll")
    fun getAllOrganizations(): Observable<BaseData<OrganizationBean>>

    /**
     * 获取所有组织
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/department/findByParent")
    fun getAllOrganizaitions(@Query("parentId") parentId: String): Observable<BaseData<OrganizationBean>>

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

    /**
     * 根据群组id查询群组人员
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/group/getAccounts")
    fun getGroupIdConstacts(@Query("groupId") Id: String): Observable<BaseData<PeopleBean>>

    /**
     * 查询群主
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/group/judgeIfCreateUser")
    fun queryGroupCreater(
        @Query("sipAccount") sipAccount: String,
        @Query("groupId") groupId: String
    ): Observable<GroupDetailsBean>


    /**
     * 修改群名称
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("/im/mobile/group/updateGroupName")
    fun updateGroupName(
        @Query("groupId") groupId: Int,
        @Query("newName") newName: String
    ): Observable<GroupDetailsBean>


    /**
     * 删除群组
     *
     * @param id
     * @return
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/group/delete")
    fun deleteGroupChat(
        @Query("groupId") id: String
    ): Observable<GroupDetailsBean>

    /**
     * 添加人员到群组
     *
     * @param id
     * @return
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/group/addGroupUser")
    fun addPeopleToGroupChat(
        @Query("groupId") groupId: String,
        @Query("ids") ids: String
    ): Observable<BaseData<PeopleBean>>


    /**
     * 删除群组人员
     *
     * @param id
     * @return
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/group/delGroupUser")
    fun deletePeopleToGroupChat(
        @Query("groupId") groupId: String,
        @Query("ids") ids: String
    ): Observable<BaseData<PeopleBean>>


}