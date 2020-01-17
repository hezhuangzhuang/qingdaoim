package com.hw.confmodule.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.confmodule.mvp.contract.ConfContract
import com.hw.confmodule.mvp.model.ConfService
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
import com.hw.provider.user.UserContants
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/9 14:27
 */
class ConfPresenter @Inject constructor() : BasePresenter<ConfContract.View>(),
    ConfContract.Presenter {
    @Inject
    lateinit var confService: ConfService

    override fun queryAllPeople() {
        checkViewAttached()

        mRootView?.showLoading()

        confService.getAllPeople()
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        var filterList = baseData.data.filter {
                            it.sip != SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT)
                        }
                        queryPeopleSuccess(filterList)
                    } else {
                        queryPeopleError(baseData.message)
                    }
                }
            }, {
                mRootView?.apply {
                    dismissLoading()
                    queryPeopleError(ExceptionHandle.handleException(it))
                }
            })
    }

    override fun createConf(confName: String,
                             duration: String,
                             accessCode: String,
                             memberSipList: String,
                             groupId: String,
                             type: Int) {

        HuaweiModuleService.createConfNetWork(confName, duration, accessCode, memberSipList, groupId, type)
    }

}