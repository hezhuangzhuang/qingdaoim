package com.hw.mylibrary.mvp.contract

import com.hw.baselibrary.common.IBaseView
import com.hw.mylibrary.data.bean.LoginBean

/**
 *author：Thinkpad
 *data:2019/12/7 07:57
 */
interface LoginContract {

    interface View : IBaseView {
        fun loginSuccess(userInfo: LoginBean)

        fun loginFail(error: String)
    }

    interface Presenter {
        fun login(name: String,
                  pwd: String,
                  deviceID: String)
    }

}