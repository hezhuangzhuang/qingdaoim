package com.hw.mylibrary.arouter

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.hw.mylibrary.mvp.model.UserService
import com.hw.provider.net.respone.user.LoginBean
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.user.IUserModuleService
import io.reactivex.Observable

/**
 *author：pc-20171125
 *data:2020/1/17 15:56
 */
@Route(path = RouterPath.UserCenter.USER_MODULE_SERVICE)
class UserModuleServiceImp : IUserModuleService {
    override fun init(context: Context?) {
    }

    override fun login(name: String, pwd: String, deviceID: String): Observable<LoginBean> {
        return UserService().login(name, pwd, deviceID)
    }
}