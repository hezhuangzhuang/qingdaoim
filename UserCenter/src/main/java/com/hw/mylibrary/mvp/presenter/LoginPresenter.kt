package com.hw.mylibrary.mvp.presenter


import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.bindLife
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.mylibrary.mvp.contract.LoginContract
import com.hw.mylibrary.mvp.model.UserService
import javax.inject.Inject

/**
 *author：Thinkpad
 *data:2019/12/7 08:39
 */
class LoginPresenter @Inject constructor() : BasePresenter<LoginContract.View>(), LoginContract.Presenter {

    @Inject
    lateinit var userService: UserService

    override fun login(name: String, pwd: String, deviceID: String) {
        //检查是否有界面
        checkViewAttached()
        mRootView?.showLoading()

        userService.login(name, pwd, deviceID)
            .bindLife(lifecycleProvider)
            .subscribe({ baseDate ->
                mRootView?.apply {
                    if (NetWorkContants.RESPONSE_CODE == baseDate.responseCode) {
                        loginSuccess(baseDate)
                    } else {
                        loginFail(baseDate.message)
                    }
                    dismissLoading()
                }
            }, { e ->
                mRootView?.apply {
                    dismissLoading()
                    loginFail(ExceptionHandle.handleException(e))
                }
            })

    }
}