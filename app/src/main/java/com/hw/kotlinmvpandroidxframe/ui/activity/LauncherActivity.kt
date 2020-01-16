package com.hw.kotlinmvpandroidxframe.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.hw.baselibrary.common.BaseApp
import com.hw.baselibrary.constant.PermissionConstants
import com.hw.baselibrary.ui.activity.BaseActivity
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.PermissionUtils
import com.hw.huaweivclib.inter.HuaweiInitImp
import com.hw.kotlinmvpandroidxframe.BuildConfig
import com.hw.kotlinmvpandroidxframe.R
import com.hw.provider.router.RouterPath

class LauncherActivity : BaseActivity() {
    override fun initData(bundle: Bundle?) {
    }

    override fun bindLayout(): Int =R.layout.activity_launcher

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
    }

    override fun doBusiness() {
        PermissionUtils.permission(PermissionConstants.STORAGE)
//            .rationale { shouldRequest -> DialogHelper.showRationaleDialog(shouldRequest) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: List<String>) {

                    //初始化华为
                    HuaweiInitImp.initHuawei(BaseApp.context, BuildConfig.APPLICATION_ID)

                    ARouter.getInstance()
                        .build(RouterPath.UserCenter.PATH_LOGIN)
                        .navigation()
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

    override fun setListeners() {
    }

    override fun onError(text: String) {
    }
}
