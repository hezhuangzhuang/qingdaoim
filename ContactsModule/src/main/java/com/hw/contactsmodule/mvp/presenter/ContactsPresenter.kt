package com.hw.contactsmodule.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.common.BaseData
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.contactsmodule.mvp.contract.ContactsContract
import com.hw.contactsmodule.mvp.model.ContactsService
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
import com.hw.provider.user.UserContants
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.ArrayList
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

        Observable.zip(
            contactsService.queryGovAccounts(),
            contactsService.queryAllPeople(),
            object : BiFunction<BaseData<PeopleBean>,
                    BaseData<PeopleBean>,
                    List<PeopleBean>> {
                override fun apply(
                    govAccounts: BaseData<PeopleBean>,
                    allAccounts: BaseData<PeopleBean>
                ): List<PeopleBean> {
                    val allList = ArrayList<PeopleBean>()

                    //判断人员是否获取完毕
                    if (NetWorkContants.RESPONSE_CODE == govAccounts.responseCode
                        && NetWorkContants.RESPONSE_CODE == allAccounts.responseCode
                    ) {
                        govAccounts.data.forEach {
                            it.sip = it.sipAccount
                        }

                        allList.addAll(govAccounts.data)
                        allList.addAll(allAccounts.data)
                    } else {
                        mRootView?.apply {
                            dismissLoading()
                            var errorMsg = ""
                            if (NetWorkContants.RESPONSE_CODE != govAccounts.responseCode) {
                                errorMsg = "特别人员获取失败,错误：${govAccounts.message}"
                            } else if (NetWorkContants.RESPONSE_CODE != allAccounts.responseCode) {
                                errorMsg = "所有联系人员获取失败,错误：${allAccounts.message}"
                            }
                            showError(errorMsg)
                        }
                    }
                    return allList
                }
            }).subscribe({
            mRootView?.apply {
                dismissLoading()
                showAllPeople(it)
            }
        })

//            contactsService.queryAllPeople()
//                .subscribe({ baseData ->
//                    mRootView?.apply {
//                        dismissLoading()
//                        if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
//                            if (baseData.data.size > 0) {
//                                //剔除自己
////                            val filter = baseData.data.filter {
////                                it.sip != SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT)
////                            }
//                                showAllPeople(baseData.data)
//                            } else {
//                                showEmptyView()
//                            }
//                        } else {
//                            showError(baseData.message)
//                        }
//                    }
//                }, {
//                    mRootView?.apply {
//                        dismissLoading()
//                        showError(ExceptionHandle.handleException(it))
//                    }
//                })
    }

    /**
     * 获取所有组织
     */
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

    /**
     * 通过组织id获取组织人员
     */
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
                            queryOrganPeopleError(baseData.message)
                        }
                    }
                },
                {
                    mRootView?.apply {
                        dismissLoading()
                        queryOrganPeopleError(ExceptionHandle.handleException(it))
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

    //群组一键起会
    override fun createConf(
        confName: String,
        duration: String,
        accessCode: String,
        groupId: String,
        type: Int
    ) {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.getGroupIdConstacts(groupId)
            .subscribe({ baseData ->
                mRootView?.apply {
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        var memberSipList = baseData.data.joinToString { it.sip }
                        HuaweiModuleService.createConfNetWork(
                            confName,
                            duration,
                            accessCode,
                            memberSipList,
                            groupId,
                            type
                        )
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
}