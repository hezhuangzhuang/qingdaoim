package com.hw.contactsmodule.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.bindLife
import com.hw.baselibrary.common.BaseData
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.contactsmodule.mvp.contract.ContactsContract
import com.hw.contactsmodule.mvp.model.ContactsService
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
import com.hw.provider.user.UserContants
import io.reactivex.Observable
import io.reactivex.functions.Function3
import java.lang.Exception
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

        var onlineNumber = 0

        Observable.zip(
            contactsService.queryGovAccounts(),
            contactsService.queryAllPeople(),
            contactsService.queryOnlineSiteList(),
            object : Function3<BaseData<PeopleBean>,
                    BaseData<PeopleBean>,
                    BaseData<PeopleBean>,
                    List<PeopleBean>> {
                override fun apply(
                    govAccounts: BaseData<PeopleBean>,
                    allAccounts: BaseData<PeopleBean>,
                    onlineSites: BaseData<PeopleBean>
                ): List<PeopleBean> {
                    val allList = ArrayList<PeopleBean>()

                    //判断人员是否获取完毕
                    if (NetWorkContants.RESPONSE_CODE == govAccounts.responseCode
                        && NetWorkContants.RESPONSE_CODE == allAccounts.responseCode
                        && 0 == onlineSites.responseCode
                    ) {
                        govAccounts.data.forEach {
                            it.sip = it.sipAccount
                        }

                        allList.addAll(govAccounts.data)
                        allList.addAll(allAccounts.data)

                        try {
                            //所有人员默认为离线状态
                            allList.forEach {
                                it.online = 1
                            }

                            onlineSites.data.forEach { onlineBean ->
                                allList.forEach {
                                    if (onlineBean.name.equals(it.name)) {
                                        //设置为在线状态
                                        it.online = 0

                                        //在线人数增加
                                        onlineNumber++
                                    }
                                }
                            }
                        } catch (e: Exception) {
//                            ToastHelper.showShort("出现异常了")
                        }
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
            })
            .bindLife(lifecycleProvider)
            .subscribe({ allPeoples ->
                mRootView?.apply {
                    dismissLoading()
                    showAllPeople(allPeoples, onlineNumber)
                }
            }, {
                mRootView?.apply {
                    LogUtils.e("获取所有通讯录-->${it.message}")
                    dismissLoading()
                    showError(it.message.toString())
                }
            })
    }

    /**
     * 获取所有组织
     */
    override fun getAllOrganizations() {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.queryAllOrganizations("0")
            .bindLife(lifecycleProvider)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        if (baseData.data.size > 0) {
                            showAllOrgan(baseData.data)
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
     * 通过depId查看子组织结构
     */
    override fun getChildOrganizations(pos: Int, depId: Int) {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.queryAllOrganizations(depId.toString())
            .bindLife(lifecycleProvider)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        if (baseData.data.size > 0) {
                            showChildOrganPeople(pos, depId,baseData.data)
                        } else {
//                            showEmptyView()
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
            .bindLife(lifecycleProvider)
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

    /**
     * 获取群聊信息
     */
    override fun getGroupChat() {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.queryGroupsById(SPStaticUtils.getInt(UserContants.USER_ID))
            .bindLife(lifecycleProvider)
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
    override fun groupChatOneCreateConf(
        confName: String,
        duration: String,
        accessCode: String,
        groupId: String,
        type: Int
    ) {
        checkViewAttached()
        mRootView?.showLoading()

        //获取群主的人员
        contactsService.getGroupIdConstacts(groupId)
            .bindLife(lifecycleProvider)
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

    /**
     * 组织机构一键起会
     */
    override fun organizationOneCreateConf(
        confName: String,
        duration: String,
        accessCode: String,
        depId: String,
        type: Int
    ) {
        checkViewAttached()
        mRootView?.showLoading()

        //获取组织机构的人员
        contactsService.queryByDepIdConstacts(depId.toInt())
            .bindLife(lifecycleProvider)
            .subscribe({ baseData ->
                mRootView?.apply {
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        //获取人员的sip
                        var memberSipList = baseData.data.joinToString { it.sip }

                        HuaweiModuleService.createConfNetWork(
                            confName,
                            duration,
                            accessCode,
                            memberSipList,
                            depId,
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