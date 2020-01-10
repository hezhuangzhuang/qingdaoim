package com.hw.confmodule.mvp.contract

import com.hw.baselibrary.common.IBaseView

/**
 *author：pc-20171125
 *data:2020/1/9 14:26
 */
interface ConfContract {

    interface View :IBaseView{
        //创建会议成功
        fun createConfSuccess()

        //创建会议失败
        fun createConfFaile()
    }

    interface Presenter{
        //创建会议
        fun createConf()
    }
}