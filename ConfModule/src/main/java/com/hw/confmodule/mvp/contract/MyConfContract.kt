package com.hw.confmodule.mvp.contract

import com.hw.baselibrary.common.IBaseView
import com.hw.confmodule.data.bean.ConfBean
import com.hw.confmodule.data.bean.HistoryConfBean

/**
 * 我的会议的接口
 */
interface MyConfContract {

    interface View : IBaseView {
        //显示会议列表
        fun showConfList(confList: List<ConfBean.DataBean>)

        //查询会议列表失败
        fun queryConfListError(errorMsg: String)

        //显示历史会议列表
        fun showHistoryList(isFirst: Boolean,historyConfList: List<HistoryConfBean.DataBean>)

        //查询历史会议列表失败
        fun queryHistoryError(isFirst: Boolean, errorMsg: String)

        //查询历史会议列表为空
        fun queryHistoryEmpty(isFirst: Boolean)
    }

    interface Presenter {
        /**
         * 获取正在召开的会议
         */
        fun getAllConfList(site: String)

        /**
         * 获取所有历史列表
         */
        fun getHistoryConfList(pageNum: Int)


    }


}