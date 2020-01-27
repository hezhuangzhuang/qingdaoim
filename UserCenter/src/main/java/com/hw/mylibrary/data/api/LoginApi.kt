package com.hw.mylibrary.data.api

import com.hw.baselibrary.common.BaseData
import com.hw.provider.net.respone.user.LoginBean
import io.reactivex.Observable
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *author：Thinkpad
 *data:2019/12/7 08:31
 */
interface LoginApi {

    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/login")
    fun login(
        @Query("userName") account: String,
        @Query("userWord") password: String,
        @Query("deviceID") deviceID: String
    ): Observable<LoginBean>

    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("im/mobile/logOut")
    fun logOut(
        @Query("sip") sip: String
    ): Observable<BaseData<String>>
}