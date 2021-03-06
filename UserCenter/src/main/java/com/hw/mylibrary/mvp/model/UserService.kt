package com.hw.mylibrary.mvp.model

import com.hw.baselibrary.common.BaseData
import com.hw.baselibrary.net.RetrofitManager
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.rx.scheduler.CustomCompose
import com.hw.mylibrary.data.api.LoginApi
import com.hw.provider.net.respone.user.LoginBean
import io.reactivex.Observable
import javax.inject.Inject

/**
 *author：Thinkpad
 *data:2019/12/7 08:11
 * 登录的接口
 */
class UserService @Inject constructor() {

    /**
     * 登录
     */
    fun login(
        account: String,
        password: String,
        deviceID: String
    ): Observable<LoginBean> {
        return RetrofitManager
            .create(LoginApi::class.java, Urls.WEBSOCKET_URL)
            .login(account, password, deviceID)
            .compose(CustomCompose())
    }

    /**
     * 登录
     */
    fun logOut(
        account: String
    ): Observable<BaseData<String>> {
        return RetrofitManager
            .create(LoginApi::class.java, Urls.WEBSOCKET_URL)
            .logOut(account)
            .compose(CustomCompose())
    }

}