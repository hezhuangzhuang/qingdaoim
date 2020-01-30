package com.hw.contactsmodule.mvp.presenter

import android.annotation.SuppressLint
import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.bindLife
import com.hw.baselibrary.common.BaseData
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.contactsmodule.data.bean.GroupDetailsBean
import com.hw.contactsmodule.mvp.contract.GroupDetailsContract
import com.hw.contactsmodule.mvp.model.ContactsService
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import java.util.ArrayList
import java.util.concurrent.TimeUnit
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
            .bindLife(lifecycleProvider)
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

        //返回的列表
        var list = ArrayList<PeopleBean>()

        contactsService.getGroupIdConstacts(groupId)
            .bindLife(lifecycleProvider)
            .flatMap(object : Function<BaseData<PeopleBean>, Observable<GroupDetailsBean>> {
                override fun apply(baseData: BaseData<PeopleBean>): Observable<GroupDetailsBean> {
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        list = baseData.data
                    }
                    return contactsService.queryGroupCreater(siteUri, groupId)
                }
            }).subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    //请求成功
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        //true:群主
                        var isCreate = baseData.data.ifCreateUser

                        if (isCreate) {
                            list.add(PeopleBean("-1", "", "", "", "", false))
                        }
                        showGroupInfo(isCreate, list)
                    } else {
                        queryGroupInfoError(baseData.message)
                    }
                }
            }, {
                mRootView?.apply {
                    dismissLoading()
                    queryGroupInfoError(ExceptionHandle.handleException(it))
                }
            })

//        //通过群组id查询是否是群主
//        contactsService.queryGroupCreater(siteUri, groupId)
//            .subscribe({ baseData ->
//                mRootView?.apply {
//                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
//                        dismissLoading()
//                        //是否是群主
////                        showGroupInfo(baseData.data.ifCreateUser,null)
//                    } else {
//                        onError(baseData.message)
//                    }
//                }
//            }, {
//                mRootView?.apply {
//                    dismissLoading()
//                    onError(ExceptionHandle.handleException(it))
//                }
//            })
    }

    //修改群名称
    override fun updateGroupName(newName: String, groupId: Int) {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.updateGroupName(groupId, newName)
            .bindLife(lifecycleProvider)
            .subscribe({ baseData ->
                mRootView?.apply {
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        dismissLoading()
                        //修改群名称结果
                        updateGroupNameResult(true, newName,groupId.toString())
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

    //删除群聊
    override fun deleteGroupChat(groupId: String) {
        checkViewAttached()
        mRootView?.showLoading()

        contactsService.deleteGroupChat(groupId)
            .bindLife(lifecycleProvider)
            .subscribe({ baseData ->
                mRootView?.apply {
                    if (NetWorkContants.RESPONSE_CODE == baseData.responseCode) {
                        //是否是群主
                        deleteGroupChatResult(groupId)
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

}