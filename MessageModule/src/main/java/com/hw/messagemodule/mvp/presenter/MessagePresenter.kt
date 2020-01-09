package com.hw.messagemodule.mvp.presenter

import com.hw.baselibrary.common.BasePresenter
import com.hw.messagemodule.mvp.contract.MessageContract
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/8 09:55
 */
class MessagePresenter @Inject constructor() : BasePresenter<MessageContract.View>(), MessageContract.Presenter {

}