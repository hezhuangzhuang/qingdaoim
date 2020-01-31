package com.hw.contactsmodule.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.bindLife
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.contactsmodule.mvp.contract.DeleteGroupPeopleContract
import com.hw.contactsmodule.mvp.model.ContactsService
import javax.inject.Inject

/**
 * 删除人员
 */
class DeleteGroupPeoplePresenter @Inject constructor() :
    BasePresenter<DeleteGroupPeopleContract.View>(), DeleteGroupPeopleContract.Presenter {
    @Inject
    lateinit var contactsService: ContactsService

    override fun deletePeoples(groupId: String, ids: String) {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.delPeopleToGroupChat(groupId, ids)
            .bindLife(lifecycleProvider)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        deletePeopleSuccess()
                    } else {
                        deletePeopleFailed(baseData.message)
                    }
                }

            }, {
                mRootView?.apply {
                    dismissLoading()
                    deletePeopleFailed(ExceptionHandle.handleException(it))
                }
            })
    }
}