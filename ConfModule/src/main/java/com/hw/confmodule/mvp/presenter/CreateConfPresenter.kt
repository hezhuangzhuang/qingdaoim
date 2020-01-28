package com.hw.confmodule.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.confmodule.mvp.contract.CreateConfContract
import com.hw.confmodule.mvp.model.CreateConfService
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
import com.hw.provider.user.UserContants
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/9 14:27
 */
class CreateConfPresenter @Inject constructor() : BasePresenter<CreateConfContract.View>(),
    CreateConfContract.Presenter {


    @Inject
    lateinit var confService: CreateConfService

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

    override fun createConf(
        confName: String,
        duration: String,
        accessCode: String,
        memberSipList: String,
        groupId: String,
        type: Int
    ) {

        HuaweiModuleService.createConfNetWork(
            confName,
            duration,
            accessCode,
            memberSipList,
            groupId,
            type
        )
    }

    /**
     * 创建群组
     */
    override fun createGroupChat(groupName: String, createId: String, ids: String) {
        checkViewAttached()
        mRootView?.showLoading()

        confService.createGroupChat(groupName, createId, ids)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (baseData.responseCode == NetWorkContants.RESPONSE_CODE) {
                        createGroupChatSuccess()
                    } else {
                        createGroupChatError(baseData.message)
                    }
                }
            }, {
                mRootView?.apply {
                    dismissLoading()
                    createGroupChatError(ExceptionHandle.handleException(it))
                }
            })
    }
}