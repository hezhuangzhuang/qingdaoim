package com.hw.kotlinmvpandroidxframe

import androidx.multidex.MultiDex
import com.hw.baselibrary.common.BaseApp
import com.hw.baselibrary.image.ImageLoader
import com.hw.kotlinmvpandroidxframe.ui.activity.LauncherActivity
import com.tencent.bugly.crashreport.CrashReport
import com.zxy.recovery.core.Recovery

/**
 *author：pc-20171125
 *data:2019/11/8 11:20
 */
class App :BaseApp() {

    override fun onCreate() {
        super.onCreate()

        //腾讯bug线上搜集工具
        CrashReport.initCrashReport(applicationContext, "9455fd103f", false)

        initCrashHandler()

        ImageLoader.init(this)

        //65535
        MultiDex.install(this);

        com.huawei.application.BaseApp.setApp(this)

        //初始化华为
//        HuaweiInitImp.initHuawei(this,BuildConfig.APPLICATION_ID)
    }

    /**
     * 异常处理
     */
    private fun initCrashHandler() {
        //         /本地提示
        Recovery.getInstance()
            .debug(true)
            .recoverInBackground(false)
            .recoverStack(true)
            .mainPage(LauncherActivity::class.java)
            .recoverEnabled(true)
            //                .callback(new MyCrashCallback())
            .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
            //                .skip(TestActivity.class)
            .init(this)
    }
}