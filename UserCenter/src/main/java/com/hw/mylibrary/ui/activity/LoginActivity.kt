package com.hw.mylibrary.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.hw.baselibrary.constant.PermissionConstants
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.PermissionUtils
import com.hw.baselibrary.utils.PhoneUtils
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.mylibrary.R
import com.hw.mylibrary.injection.component.DaggerUserComponent
import com.hw.mylibrary.injection.module.UserModule
import com.hw.mylibrary.mvp.contract.LoginContract
import com.hw.mylibrary.mvp.presenter.LoginPresenter
import com.hw.provider.huawei.commonservice.localbroadcast.CustomBroadcastConstants
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcast
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcastReceiver
import com.hw.provider.net.respone.user.LoginBean
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
import com.hw.provider.user.UserContants
import kotlinx.android.synthetic.main.activity_login.*

@Route(path = RouterPath.UserCenter.PATH_LOGIN)
class LoginActivity : BaseMvpActivity<LoginPresenter>(), LoginContract.View {

    override fun initComponent() {
        DaggerUserComponent.builder()
            .activityComponent(mActivityComponent)
            .userModule(UserModule())
            .build()
            .inject(this)
        mPresenter.mRootView = this
    }

    override fun initData(bundle: Bundle?) {
    }

    override fun bindLayout(): Int = R.layout.activity_login

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        etUser.setText(SPStaticUtils.getString(UserContants.USER_NAME))
        etPwd.setText(SPStaticUtils.getString(UserContants.PASS_WORD))
    }

    override fun doBusiness() {
    }

    override fun setListeners() {
        btLogin.setOnClickListener {
            requestPhonePermission()
        }
    }

    fun requestPhonePermission() {
        PermissionUtils.permission(PermissionConstants.PHONE)
//            .rationale { shouldRequest -> DialogHelper.showRationaleDialog(shouldRequest) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: List<String>) {
                    LogUtils.d(permissionsGranted)
                    //登录的请求
                    loginRequest()
                }

                override fun onDenied(
                    permissionsDeniedForever: List<String>,
                    permissionsDenied: List<String>
                ) {
                    LogUtils.d(permissionsDeniedForever, permissionsDenied)
                    if (!permissionsDeniedForever.isEmpty()) {
                        return
                    }
                    finish()
                }
            })
            .request()
    }

    @SuppressLint("MissingPermission")
    private fun loginRequest() {
        //注册广播
        LocBroadcast.getInstance().registerBroadcast(loginReceiver, mActions)

        var name = etUser.text.toString().trim()
        var pwd = etPwd.text.toString().trim()
        mPresenter.login(name, pwd, PhoneUtils.getDeviceId())
    }

    override fun onError(text: String) {
    }

    override fun loginSuccess(userInfo: LoginBean) {
        //保存用户信息
        saveUserInfo(userInfo)

        //开始登录华为
        userInfo.data.apply {
            HuaweiModuleService.login(
                sipAccount,
                sipPassword,
                scIp,
                scPort
            )
        }
    }

    /**
     * 保存用户信息
     */
    private fun saveUserInfo(userInfo: LoginBean) {
        //是否登录
        SPStaticUtils.put(UserContants.HAS_LOGIN, true)

        //显示的名称
        SPStaticUtils.put(UserContants.DISPLAY_NAME, userInfo.data.name)

        //用户id
        SPStaticUtils.put(UserContants.USER_ID, userInfo.data.id)

        //登录的用户名密码
        SPStaticUtils.put(UserContants.USER_NAME, userInfo.data.accountName)
        SPStaticUtils.put(UserContants.PASS_WORD, userInfo.data.password)

        //华为登录密码
        SPStaticUtils.put(UserContants.HUAWEI_ACCOUNT, userInfo.data.sipAccount)
        SPStaticUtils.put(UserContants.HUAWEI_PWD, userInfo.data.sipPassword)

        //华为登录地址
        SPStaticUtils.put(UserContants.HUAWEI_SMC_IP, userInfo.data.scIp)
        SPStaticUtils.put(UserContants.HUAWEI_SMC_PORT, userInfo.data.scPort)
    }

    override fun loginFail(error: String) {
        ToastHelper.showShort(error)
    }

    override fun onDestroy() {
        super.onDestroy()

        //取消注册广播
        LocBroadcast.getInstance().unRegisterBroadcast(loginReceiver, mActions)
    }

    /*华为登录相关start*/
    var mActions = arrayOf<String>(
        CustomBroadcastConstants.LOGIN_SUCCESS,
        CustomBroadcastConstants.LOGIN_FAILED,
        CustomBroadcastConstants.LOGOUT
    )

    private val loginReceiver = object : LocBroadcastReceiver {
        override fun onReceive(broadcastName: String?, obj: Any?) {
            Log.i(TAG, "loginReceiver-->$broadcastName")
            when (broadcastName) {
                CustomBroadcastConstants.LOGIN_SUCCESS -> {
                    dismissLoading()

                    ToastHelper.showShort("登录成功")

                    ARouter.getInstance()
                        .build(RouterPath.Main.PATH_MAIN)
                        .navigation(application,object :NavigationCallback{
                            override fun onLost(postcard: Postcard?) {
                            }

                            override fun onFound(postcard: Postcard?) {
                            }

                            override fun onInterrupt(postcard: Postcard?) {
                            }

                            override fun onArrival(postcard: Postcard?) {
                                finish()
                            }
                        })
                }

                CustomBroadcastConstants.LOGIN_FAILED -> {
                    ToastHelper.showShort("登录华为失败")
                }

                CustomBroadcastConstants.LOGOUT -> {
                    ToastHelper.showShort("登出")
                }

                else -> {
                }
            }
        }
    }
}
