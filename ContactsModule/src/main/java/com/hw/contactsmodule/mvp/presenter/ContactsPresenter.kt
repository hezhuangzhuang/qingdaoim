package com.hw.contactsmodule.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.contactsmodule.mvp.contract.ContactsContract
import com.hw.contactsmodule.mvp.model.ContactsService
import com.hw.provider.user.UserContants
import javax.inject.Inject


/**
 *author：pc-20171125
 *data:2020/1/8 11:56
 */
class ContactsPresenter @Inject constructor() : BasePresenter<ContactsContract.View>(),
    ContactsContract.Presenter {


    @Inject
    lateinit var contactsService: ContactsService

    override fun getAllPeople() {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.queryAllPeople()
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        if (baseData.data.size > 0) {
                            val filter = baseData.data.filter {
                                it.sip != SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT)
                            }
                            showAllPeople(filter)
                        } else {
                            showEmptyView()
                        }
                    } else {
                        showError(baseData.message)
                    }
                }
            }, {
                mRootView?.apply {
                    dismissLoading()
                    showError(ExceptionHandle.handleException(it))
                }
            })
    }

    override fun getAllOrganizations() {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.queryAllOrganizations()
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        if (baseData.data.size > 0) {
                            showOrgan(baseData.data)
                        } else {
                            showEmptyView()
                        }
                    } else {
                        showError(baseData.message)
                    }
                }
            }, {
                mRootView?.apply {
                    dismissLoading()
                    showError(ExceptionHandle.handleException(it))
                }
            }
            )
    }

    override fun getDepIdConstacts(pos: Int, depId: Int) {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.queryByDepIdConstacts(depId)
            .subscribe(
                { baseData ->
                    mRootView?.apply {
                        dismissLoading()
                        if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                            showOrganPeople(pos, baseData.data)
                        } else {
                            showError(baseData.message)
                        }
                    }
                },
                {
                    mRootView?.apply {
                        dismissLoading()
                        showError(ExceptionHandle.handleException(it))
                    }
                })
    }

    override fun getGroupChat() {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.queryGroupsById(SPStaticUtils.getInt(UserContants.USER_ID))
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        if (baseData.data.size > 0) {
                            showGroupChat(baseData.data)
                        } else {
                            showEmptyView()
                        }
                    } else {
                        showError(baseData.message)
                    }
                }
            },
                {
                mRootView?.apply {
                    dismissLoading()
                    showError(ExceptionHandle.handleException(it))
                }
            })
    }

}