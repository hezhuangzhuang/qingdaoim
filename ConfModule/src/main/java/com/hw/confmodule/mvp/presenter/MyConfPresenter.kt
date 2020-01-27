package com.hw.confmodule.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.confmodule.mvp.contract.MyConfContract
import com.hw.confmodule.mvp.model.CreateConfService
import io.reactivex.internal.util.ExceptionHelper
import javax.inject.Inject

class MyConfPresenter @Inject constructor() : BasePresenter<MyConfContract.View>(),
    MyConfContract.Presenter {


    @Inject
    lateinit var createService: CreateConfService

    /**
     * 获取所有会议
     */
    override fun getAllConfList(site: String) {
        checkViewAttached()
        mRootView?.showLoading()

        createService.getAllConf(site)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (baseData.msg == NetWorkContants.SUCCESS) {
                        showConfList(baseData.data)
                    } else {
                        queryConfListError(baseData.msg)
                    }
                }
            }, {
                mRootView?.apply {
                    dismissLoading()
                    queryConfListError(ExceptionHandle.handleException(it))
                }
            })
    }

    /**
     * 查询历史会议
     */
    override fun getHistoryConfList(pageNum: Int) {
        checkViewAttached()
        mRootView?.showLoading()

        createService.getHistoryConfList(pageNum)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (baseData.responseCode == NetWorkContants.RESPONSE_CODE) {
                        if (baseData.data.isEmpty()) {
                            queryHistoryEmpty(1 == pageNum)
                        } else {
                            showHistoryList(1 == pageNum, baseData.data)
                        }

                    } else {
                        queryHistoryError(1 == pageNum, baseData.message)
                    }
                }
            }, {
                mRootView?.apply {
                    dismissLoading()
                    queryHistoryError(1 == pageNum, ExceptionHandle.handleException(it))
                }
            })
    }

    /**
     * 创建群组
     */


}