package com.hw.kotlinmvpandroidxframe.push.huawei

import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.hw.baselibrary.net.RetrofitManager
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.rx.scheduler.CustomCompose
import com.hw.baselibrary.rx.scheduler.SchedulerUtils
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.contactsmodule.data.api.ContactsApi
import com.hw.kotlinmvpandroidxframe.net.api.PushApi
import com.hw.provider.user.UserContants
import java.lang.Exception

class PushHmsMessageService : HmsMessageService() {

    val TAG = "PushHmsMessageService"

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        LogUtils.i(TAG, "onNewToken-->" + token!!)
        saveHuaweiPushToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        LogUtils.i(TAG, "onMessageReceived-->" + remoteMessage!!.toString())
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()

        LogUtils.i(TAG, "onDeletedMessages-->")
    }

    override fun onMessageSent(s: String?) {
        super.onMessageSent(s)

        LogUtils.i(TAG, "onMessageSent-->" + s!!)
    }

    /**
     * 保存华为的推送token
     */
    private fun saveHuaweiPushToken(token: String?) {
        try{
            RetrofitManager
                .create(PushApi::class.java, Urls.FILE_URL)
                .saveHuaweiToken(SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT),token!!)
                .compose(CustomCompose())
                .subscribe({
                    LogUtils.i(TAG, "saveHuaweiPushToken-->onSuccess-->" + it.toString())
                },{
                    LogUtils.i(TAG, "saveHuaweiPushToken-->onError-->" + it.toString())
                })
        }catch (e:Exception){

        }


//        RetrofitManager.create(PushApi::class.java, Urls.FILE_URL)
//            .saveHuaweiToken(SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT),token!!)
//            .compose(SchedulerUtils.ioToMain())
//            .subscribe({
//                LogUtils.i(TAG, "saveHuaweiPushToken-->onSuccess-->" + it.toString())
////                ToastHelper.showShort(it.toString())
//            });
//        val sipAccount = PreferenceUtil.getString(this, Constant.SIP_ACCOUNT, "")
//        HttpUtils.getInstance(this)
//            .getRetofitClinet()
//            .setBaseUrl(Urls.BASE_URL)
//            .builder(ConfApi::class.java)
//            .saveHuaweiToken(sipAccount, token)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : RxSubscriber<BaseData_BackServer>() {
//                fun onSuccess(logicData: BaseData_BackServer) {
//                    LogUtils.i(TAG, "saveHuaweiPushToken-->onSuccess-->" + logicData.toString())
//                }
//
//                protected fun onError(responeThrowable: ResponeThrowable) {
//                    ToastUtil.showShortToast(applicationContext, responeThrowable.getMessage())
//                }
//            })

    }
}
