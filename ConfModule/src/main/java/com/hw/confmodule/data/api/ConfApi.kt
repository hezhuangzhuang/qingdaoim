package com.hw.confmodule.data.api

import com.hw.baselibrary.common.BaseData
import com.hw.confmodule.data.bean.ConfBean
import com.hw.confmodule.data.bean.HistoryConfBean
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

interface ConfApi {

    /**
     * 获取所有会议列表
     */
    @POST("conf/queryBySiteUri")
    fun getAllConfList(@Body body: RequestBody): Observable<ConfBean>


    /**
     * 获取历史会议列表
     */
    @GET("im/confInfo/queryListByMobile")
    fun getHistoryConfList(
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int = 10
    ): Observable<HistoryConfBean>


    /**
     * 创建群组
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/group/createGroup")
    fun createGroupChat(
        @Query("groupName") groupName: String,
        @Query("createId") createId: String,
        @Query("ids") ids: String
    ): Observable<BaseData<String>>

    /**
     * 创建群组
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST
    fun createGroupChat(
        @Url url: String,
        @Query("groupName") groupName: String,
        @Query("createId") createId: String,
        @Query("ids") ids: String
    ): Observable<BaseData<String>>
}