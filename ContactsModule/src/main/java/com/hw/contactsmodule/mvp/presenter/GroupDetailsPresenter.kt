package com.hw.contactsmodule.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.contactsmodule.mvp.contract.GroupDetailsContract
import com.hw.contactsmodule.mvp.model.ContactsService
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
import javax.inject.Inject

class GroupDetailsPresenter @Inject constructor() : BasePresenter<GroupDetailsContract.View>(),
    GroupDetailsContract.Presenter {

    @Inject
    lateinit var contactsService: ContactsService

    override fun queryPeopleByGroupId(groupId: String) {
        checkViewAttached()
        mRootView?.showLoading()

        //通过群组id查询群组人员
        contactsService.getGroupIdConstacts(groupId)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        showGroupChatPeople(baseData.data)
                    } else {
                        onError(baseData.message)
                    }
                }
            }, {
                mRootView?.apply {
                    dismissLoading()
                    onError(ExceptionHandle.handleException(it))
                }
            })
    }

    override fun queryGroupCreater(siteUri: String, groupId: String) {
        checkViewAttached()
        mRootView?.showLoading()

        //通过群组id查询群组人员
        contactsService.queryGroupCreater(siteUri, groupId)
            .subscribe({ baseData ->
                mRootView?.apply {
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        dismissLoading()
                        //是否是群主
                        showGroupInfo(baseData.data.ifCreateUser)
                    } else {
                        onError(baseData.message)
                    }
                }
            }, {
                mRootView?.apply {
                    dismissLoading()
                    onError(ExceptionHandle.handleException(it))
                }
            })
    }

    //修改群名称
    override fun updateGroupName(newName: String, groupId: Int) {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.updateGroupName(groupId,newName)
            .subscribe({baseData->
                mRootView?.apply {
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        dismissLoading()
                        //是否是群主
                        updateGroupNameResult(true)
                    } else {
                        onError(baseData.message)
                    }
                }
            },{
                mRootView?.apply {
                    dismissLoading()
                    onError(ExceptionHandle.handleException(it))
                }
            })
    }

    //删除群聊
    override fun deleteGroupChat(groupId: String) {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.deleteGroupChat(groupId)
            .subscribe({baseData->
                mRootView?.apply {
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        //是否是群主
                        deleteGroupChatResult(groupId)
                    } else {
                        onError(baseData.message)
                    }
                }
            },{
                mRootView?.apply {
                    dismissLoading()
                    onError(ExceptionHandle.handleException(it))
                }
            })
    }

}