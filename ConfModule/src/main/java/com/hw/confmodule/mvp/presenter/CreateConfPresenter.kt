package com.hw.confmodule.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.bindLife
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.confmodule.mvp.contract.CreateConfContract
import com.hw.confmodule.mvp.model.CreateConfService
import com.hw.provider.router.provider.constacts.impl.ContactsModuleRouteService
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
import com.hw.provider.user.UserContants
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/9 14:27
 */
class CreateConfPresenter @Inject constructor() : BasePresenter<CreateConfContract.View>(),
    CreateConfContract.Presenter {

    /**
     * 添加人员到群组
     */
    override fun addPeoplesToGroup(groupId: String, ids: String) {
        checkViewAttached()
        mRootView?.showLoading()

        ContactsModuleRouteService.addPeopleToGroupChat(groupId, ids)
            .bindLife(lifecycleProvider)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        addPeopleToGroupChatSuccess()
                    } else {
                        addPeopleToGroupChatFaile(baseData.message)
                    }
                }
            }, {
                mRootView?.apply {
                    dismissLoading()
                    addPeopleToGroupChatFaile(ExceptionHandle.handleException(it))
                }
            })
    }

    /**
     * 预约会议
     */
    override fun reservedConf(
        confName: String,
        duration: String,
        accessCode: String,
        memberSipList: String,
        groupId: String,
        type: Int,
        confType: String,
        startTime: String
    ) {

        checkViewAttached()
        mRootView?.showLoading()

        val reservedConfSuccess = HuaweiModuleService.reservedConfNetWork(
            confName,
            duration,
            accessCode,
            memberSipList,
            groupId,
            type,
            confType,
            startTime
        )

        mRootView?.apply {
            if (reservedConfSuccess) {
                reservedConfSuccess()
            } else {
                reservedConfFaile()
            }
            dismissLoading()
        }
    }


    @Inject
    lateinit var confService: CreateConfService

    override fun queryAllPeople() {
        checkViewAttached()
        mRootView?.showLoading()

        confService.getAllPeople()
            .bindLife(lifecycleProvider)
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
            .bindLife(lifecycleProvider)
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