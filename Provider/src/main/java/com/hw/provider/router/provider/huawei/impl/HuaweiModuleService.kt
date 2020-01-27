package com.hw.provider.router.provider.huawei.impl

import com.alibaba.android.arouter.launcher.ARouter
import com.hw.provider.router.provider.huawei.IHuaweiModuleService

/**
 *author：pc-20171125
 *data:2020/1/16 10:18
 */
object HuaweiModuleService {
    val navigation =
        ARouter.getInstance().navigation(IHuaweiModuleService::class.java)

    /**
     * 登录的方法
     */
    fun login(
        userName: String,
        password: String,
        smcRegisterServer: String,
        smcRegisterPort: String
    ) {
        navigation.login(userName, password, smcRegisterServer, smcRegisterPort)
    }

    /**
     * 登出
     */
    fun logOut() {
        navigation.logOut()
    }

    /**
     * 呼叫会场
     */
    fun callSite(siteNumber: String, isVideoCall: Boolean) {
        navigation.callSite(siteNumber, isVideoCall)
    }

    /**
     * 通过后台接口召集会议
     * @param confName      会议名称
     * @param duration      会议时长，单位(分钟)
     * @param memberSipList 参会人员的sip号码，多个以逗号分隔
     * @param groupId
     * @param accessCode    会议接入码
     * @param type          0：语音会议，1：视频会议
     */
    fun createConfNetWork(
        confName: String,
        duration: String,
        accessCode: String,
        memberSipList: String,
        groupId: String,
        type: Int) {
        navigation.createConfNetWork(confName, duration, accessCode, memberSipList, groupId, type)
    }

    /**
     * 呼叫会场
     */
    fun callSite(accessCode: String) {
         navigation.joinConf(accessCode)
    }
}