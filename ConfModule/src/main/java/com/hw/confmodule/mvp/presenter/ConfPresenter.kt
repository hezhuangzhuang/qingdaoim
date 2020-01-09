package com.hw.confmodule.mvp.presenter

import com.hw.baselibrary.common.BasePresenter
import com.hw.confmodule.mvp.contract.ConfContract
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/9 14:27
 */
class ConfPresenter @Inject constructor() : BasePresenter<ConfContract.View>(),
    ConfContract.Presenter {

}